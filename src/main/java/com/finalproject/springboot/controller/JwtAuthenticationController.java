package com.finalproject.springboot.controller;

import com.finalproject.springboot.model.dao.UserDAO;
import com.finalproject.springboot.model.dto.JwtResponseDTO;
import com.finalproject.springboot.model.jwt.JwtRequest;
import com.finalproject.springboot.model.jwt.JwtResponse;
import com.finalproject.springboot.repository.UserRepo;
import com.finalproject.springboot.service.JwtUserDetailsService;
import com.finalproject.springboot.model.dto.UserDTO;
import com.finalproject.springboot.config.JwtTokenUtil;
import com.finalproject.springboot.service.TokenBlacklistService;
import com.finalproject.springboot.util.CustomErrorType;
import com.finalproject.springboot.config.JwtBlacklistFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class JwtAuthenticationController {
	public static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);
	@Autowired
	private PasswordEncoder bcryptEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private JwtUserDetailsService userDetailsService;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private TokenBlacklistService tokenBlacklistService;
	@Autowired
	private JwtBlacklistFilter jwtBlacklistFilter;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		// check registered username
		final UserDAO user = userRepo.findByUsername(authenticationRequest.getUsername());
		if (user == null) {
			logger.error("Unable to login. Username of {} is not found.", authenticationRequest.getUsername());
			return new ResponseEntity<>(new CustomErrorType("Login Failed: We could not find your account."),
					HttpStatus.NOT_FOUND);
		}
		// check password
		if (!(bcryptEncoder.matches(authenticationRequest.getPassword(), user.getPassword()))) {
			logger.error("Unable to login. Password is wrong.");
			return new ResponseEntity<>(new CustomErrorType("Login Failed: Wrong Password. " +
					"Please re-enter the password."), HttpStatus.FORBIDDEN);
		}
		// check user status
		if (user.getStatus() != 1) {
			logger.error("Unable to login. Username of {} is inactive.", authenticationRequest.getUsername());
			return new ResponseEntity<>(new CustomErrorType("Login Failed: Your account is inactive. Please call the admin."),
					HttpStatus.NOT_ACCEPTABLE);
		}
		// authenticate
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		// load and set user_id & role
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		JwtResponseDTO responseDTO = new JwtResponseDTO();
		responseDTO.setId(user.getId());
		responseDTO.setRole(user.getRole());
		// load and set token
		final JwtResponse jwtResponse = new JwtResponse(jwtTokenUtil.generateToken(userDetails));
		responseDTO.setToken(jwtResponse.getToken());

		return ResponseEntity.ok(responseDTO);
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		boolean regexUsername = userDetailsService.usernameRegex(user.getUsername());
		boolean regexPassword = userDetailsService.passwordRegex(user.getPassword());
		boolean regexEmail = userDetailsService.emailRegex(user.getEmail());

		boolean isExist = userDetailsService.existUsername(user.getUsername());

		if (regexUsername && regexPassword && regexEmail) {
			if (!isExist) {
				return ResponseEntity.ok(userDetailsService.save(user));
			} else {
				return new ResponseEntity<>(new CustomErrorType("Username has been already used. Please change the username."),
						HttpStatus.valueOf(410));
			}
		} else if (!regexUsername && regexPassword && regexEmail) {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the username."),
					HttpStatus.valueOf(411));
		} else if (regexUsername && !regexPassword && regexEmail) {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the password."),
					HttpStatus.valueOf(412));
		} else if (regexUsername && regexPassword && !regexEmail) {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the email."),
					HttpStatus.valueOf(413));
		} else if (!regexUsername && !regexPassword && regexEmail) {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the username and password."),
					HttpStatus.valueOf(414));
		} else if (regexUsername && !regexPassword && !regexEmail) {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the password and email."),
					HttpStatus.valueOf(415));
		} else if (!regexUsername && regexPassword && !regexEmail) {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the username and email."),
					HttpStatus.valueOf(416));
		} else {
			return new ResponseEntity<>(new CustomErrorType("Wrong regex. Please re-enter the username, password, and email."),
					HttpStatus.valueOf(417));
		}
	}

	@PostMapping(value = "/api/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) throws Exception {
		String token = jwtBlacklistFilter.extractToken(request);
		tokenBlacklistService.addToBlacklist(token);

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Logout Successful!");
		return ResponseEntity.ok(response);
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}