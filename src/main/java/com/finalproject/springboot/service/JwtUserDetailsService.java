package com.finalproject.springboot.service;

import com.finalproject.springboot.model.dto.UserDTO;
import com.finalproject.springboot.model.dao.UserDAO;
import com.finalproject.springboot.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	public static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Loading User : {}", username);
		UserDAO user = userRepo.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
	}

	public UserDAO save(UserDTO user) {
		UserDAO newUser = new UserDAO();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setName(user.getName());
		newUser.setEmail(user.getEmail());
		newUser.setContact(user.getContact());
		newUser.setAddress(user.getAddress());
		newUser.setRole(user.getRole());
		newUser.setStatus(1);
		return userRepo.save(newUser);
	}

	public boolean usernameRegex(String username) {
		boolean regexUsername;
		boolean uname_regex = Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{6,}$", username);
		if (uname_regex) {
			regexUsername = true;
		} else {
			regexUsername = false;
		}
		return regexUsername;
	}

	public boolean passwordRegex(String password) {
		boolean regexPassword;
		boolean pass_regex = Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
				password);
		if (pass_regex) {
			regexPassword = true;
		} else {
			regexPassword = false;
		}
		return regexPassword;
	}

	public boolean emailRegex(String email) {
		boolean regexEmail;
		boolean email_regex = Pattern.matches("([a-zA-Z0-9]+(?:[._+-][a-zA-Z0-9]+)*)@([a-zA-Z0-9]+(?:[.-][a-zA-Z0-9]+)*[.][a-zA-Z]{2,})",
				email);
		if (email_regex) {
			regexEmail = true;
		} else {
			regexEmail = false;
		}
		return regexEmail;
	}

	public boolean existUsername(String username) {
		boolean isExist = false;
		List<UserDAO> users = (List<UserDAO>) userRepo.findAll();
		for (UserDAO curr: users) {
			if (username.equals(curr.getUsername())) {
				isExist = true;
				break;
			} else {
				isExist = false;
			}
		}
		return isExist;
	}
}