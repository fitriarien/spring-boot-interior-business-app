package com.finalproject.springboot.controller;

import com.finalproject.springboot.model.dao.UserDAO;
import com.finalproject.springboot.model.dto.UserDTO;
import com.finalproject.springboot.repository.UserRepo;
import com.finalproject.springboot.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class UserRestController {
    public static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    @Autowired
    UserRepo userRepo;

    // -------------------------- View User Profile -----------------------------------------
    @RequestMapping(value = "/user/view/{user_id}", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<?> viewProfile(@PathVariable("user_id") String user_id) throws SQLException, ClassNotFoundException {
        logger.info("Fetching Profile with id {}", user_id);
        UserDAO userDAO;
        try {
            long userIdLong = Long.parseLong(user_id);
            userDAO = userRepo.findById(userIdLong).orElse(null);
        } catch (Exception e) {
            logger.error("Unable to view. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to view. User id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        if (userDAO == null) {
            logger.error("User with id {} not found.", user_id);
            return new ResponseEntity<>(new CustomErrorType("User with id " + user_id  + " is not found"),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(userDAO, HttpStatus.OK);
    }

    // -------------------------- Update Profile -----------------------------------------
    @RequestMapping(value = "/user/update/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProfile(@PathVariable("user_id") String user_id, @RequestBody UserDTO userDTO) throws SQLException, ClassNotFoundException {
        logger.info("Updating Profile with id {}", user_id);
        UserDAO currUser;
        try {
            long userIdLong = Long.parseLong(user_id);
            currUser = userRepo.findById(userIdLong).orElse(null);
        } catch (Exception e) {
            logger.error("Unable to update. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to update. User id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        if (currUser == null) {
            logger.error("Unable to update. User with id {} not found.", user_id);
            return new ResponseEntity<>(new CustomErrorType("Unable to update. User with id " + user_id
                    + " is not found."), HttpStatus.NOT_FOUND);
        }

        // map data to DAO
        currUser.setName(userDTO.getName());
        currUser.setEmail(userDTO.getEmail());
        currUser.setContact(userDTO.getContact());
        currUser.setAddress(userDTO.getAddress());

        // save mapped data
        userRepo.save(currUser);
        return new ResponseEntity<>(currUser, HttpStatus.OK);
    }

    // -------------------------- Delete Account -----------------------------------------
    @RequestMapping(value = "/user/delete/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> inactiveAccount(@PathVariable("user_id") String user_id) throws SQLException, ClassNotFoundException {
        logger.info("Fetching & Deleting User with id {}", user_id);
        UserDAO userDAO;
        try {
            long userIdLong = Long.parseLong(user_id);
            userDAO = userRepo.findById(userIdLong).orElse(null);
        } catch (Exception e) {
            logger.error("Unable to delete. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to delete. User id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        if (userDAO == null) {
            logger.error("Unable to delete. User with id {} not found.", user_id);
            return new ResponseEntity<>(new CustomErrorType("Unable to delete. User with id " + user_id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        userDAO.setStatus(0);
        userRepo.save(userDAO);
        Map<String, Object> response = new HashMap<>();
        response.put("message:", "Delete Account: Success!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
