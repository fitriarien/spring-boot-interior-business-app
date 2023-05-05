package com.finalproject.springboot.controller;

import com.finalproject.springboot.model.dao.*;
import com.finalproject.springboot.model.dto.ImageDTO;
import com.finalproject.springboot.repository.ImageRepo;
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
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class ImageRestController {
    public static final Logger logger = LoggerFactory.getLogger(ImageRestController.class);
    @Autowired
    ImageRepo imageRepo;
    @Autowired
    UserRepo userRepo;

    // ------------------- Upload Image -------------------------------------------
    @RequestMapping(value = "/image/{user_id}", method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<?> uploadImage(@PathVariable("user_id") String user_id,
                                         @RequestBody ImageDTO imageDTO) throws SQLException, ClassNotFoundException {
        // check data type
        long userIdLong;
        try {
            userIdLong = Long.parseLong(user_id);
        } catch (Exception e) {
            logger.error("Unable to update. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to update. User id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        // check user role & status
        logger.info("Checking the role with user_id : {} before creating product", user_id);
        UserDAO currUser = userRepo.findById(userIdLong).orElse(null);
        String currRole = currUser.getRole();
        int currStatus = currUser.getStatus();

        if (currRole.equalsIgnoreCase("admin")) {
            if (currStatus == 1) {
                logger.info("Upload Image : {}", imageDTO);

                ImageDAO currImage = new ImageDAO();
                currImage.setImage_name(imageDTO.getImage_name());
                currImage.setImage(imageDTO.getImage());
                currImage.setImage_status(1);
                currImage.setUserDAO(currUser); // set user id who updates

                imageRepo.save(currImage);
                return new ResponseEntity<>(currImage, HttpStatus.CREATED);
            } else {
                logger.error("Unable to upload image. User status is inactive.");
                return new ResponseEntity<>(new CustomErrorType("Unable to upload. User status is inactive"),
                        HttpStatus.FORBIDDEN);
            }
        } else {
            logger.error("Unable to upload image. User role is not admin.");
            return new ResponseEntity<>(new CustomErrorType("Unable to upload image. The role of " + currRole +
                    " doesn't have permission to upload image."),
                    HttpStatus.FORBIDDEN);
        }
    }

    // ------------------- Delete Image -----------------------------------------
    @RequestMapping(value = "/image/{image_id}/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> deleteProduct(@PathVariable("image_id") String image_id,
                                           @PathVariable("user_id") String user_id) throws SQLException, ClassNotFoundException {
        // check data type
        long imageIdLong;
        long userIdLong;
        try {
            imageIdLong = Long.parseLong(image_id);
            userIdLong = Long.parseLong(user_id);
        } catch (Exception e) {
            logger.error("Unable to update. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to update. ID must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        // check the role
        logger.info("Checking the role with user_id : {} before creating product", user_id);
        UserDAO currUser = userRepo.findById(userIdLong).orElse(null);
        String currRole = currUser.getRole();
        int currStatus = currUser.getStatus();

        if (currRole.equalsIgnoreCase("admin")) {
            if (currStatus == 1) {
                logger.info("Fetching & Deleting Image with id {}", image_id);
                ImageDAO currImage = imageRepo.findById(imageIdLong).orElse(null);
                if (currImage == null) {
                    logger.error("Unable to delete. Image with id {} not found.", image_id);
                    return new ResponseEntity<>(new CustomErrorType("Unable to delete. Image with id "
                            + image_id + " is not found."), HttpStatus.NOT_FOUND);
                }
                currImage.setImage_status(0);
                imageRepo.save(currImage);
                Map<String, Object> response = new HashMap<>();
                response.put("message:", "Delete Image: Success!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.error("Unable to delete image. User status is inactive.");
                return new ResponseEntity<>(new CustomErrorType("Unable to delete. User status is inactive"),
                        HttpStatus.FORBIDDEN);
            }
        } else {
            logger.error("Unable to delete image. User role is not admin.");
            return new ResponseEntity<>(new CustomErrorType("Unable to delete. The role of " + currRole +
                    " doesn't have permission to delete image."), HttpStatus.FORBIDDEN);
        }
    }

    // ------------------- Retrieve All Images --------------------------------------------
    @RequestMapping(value = "/image/", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<List<ImageDAO>> getAllImages() throws SQLException, ClassNotFoundException {
        List<ImageDAO> images = imageRepo.findAll(); //direct jpa method
        if (images.isEmpty()) {
            return new ResponseEntity<>(images, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(images, HttpStatus.OK);
    }
}
