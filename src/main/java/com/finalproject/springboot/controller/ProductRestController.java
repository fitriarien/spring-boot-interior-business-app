package com.finalproject.springboot.controller;

import com.finalproject.springboot.model.dao.ImageDAO;
import com.finalproject.springboot.model.dao.ProductDAO;
import com.finalproject.springboot.model.dao.UserDAO;
import com.finalproject.springboot.model.dto.ProductDTO;
import com.finalproject.springboot.repository.ImageRepo;
import com.finalproject.springboot.repository.ProductRepo;
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
public class ProductRestController {
    public static final Logger logger = LoggerFactory.getLogger(ProductRestController.class);
    @Autowired
    ProductRepo productRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ImageRepo imageRepo;

    // ------------------- Retrieve All Products --------------------------------------------
    @RequestMapping(value = "/product/", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<List<ProductDAO>> getAllProduct() throws SQLException, ClassNotFoundException {
        List<ProductDAO> products = productRepo.findAll(); //direct jpa method
        if (products.isEmpty()) {
            return new ResponseEntity<>(products, HttpStatus.NOT_FOUND);
        }

        logger.info("Step...");
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ------------------- Retrieve Single Product --------------------------------------------
    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getProduct(@PathVariable("id") String id) throws SQLException, ClassNotFoundException {
        logger.info("Fetching Product with id {}", id);

        ProductDAO product;
        try {
            long prodIdLong = Long.parseLong(id);
            product = productRepo.findById(prodIdLong).orElse(null);
        } catch (Exception e) {
            logger.error("Unable to view. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to view. Id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        if (product == null) {
            logger.error("Product with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Product with id " + id  + " is not found"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // ------------------- Create a Product -------------------------------------------
    @RequestMapping(value = "/product/{user_id}", method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<?> createProduct(@PathVariable("user_id") String user_id,
                                           @RequestBody ProductDTO productDTO) throws SQLException, ClassNotFoundException {
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
        ImageDAO currImage = imageRepo.findById(productDTO.getImage_id()).orElse(null);
        String currRole = currUser.getRole();
        int currStatus = currUser.getStatus();

        if (currRole.equalsIgnoreCase("admin")) {
            if (currStatus == 1) {
                if (productDTO.getProduct_name() == "") {
                    logger.error("Unable to create product. Product name cannot be null.");
                    return new ResponseEntity<>(new CustomErrorType("Unable to create product. Product name cannot be null."),
                            HttpStatus.FORBIDDEN);
                }

                logger.info("Creating Product : {}", productDTO);
                ProductDAO currProduct = new ProductDAO();
                currProduct.setProduct_name(productDTO.getProduct_name());
                currProduct.setProduct_model(productDTO.getProduct_model());
                currProduct.setEstimated_cost(productDTO.getEstimated_cost());
                currProduct.setProduct_status(1);
                currProduct.setImageDAO(currImage);
                currProduct.setUserDAO(currUser);

                productRepo.save(currProduct);
                return new ResponseEntity<>(currProduct, HttpStatus.CREATED);
            } else {
                logger.error("Unable to create product. User status is inactive.");
                return new ResponseEntity<>(new CustomErrorType("Unable to create. User status is inactive"),
                        HttpStatus.FORBIDDEN);
            }
        } else {
            logger.error("Unable to create product. User role is not admin.");
            return new ResponseEntity<>(new CustomErrorType("Unable to create. The role of " + currRole +
                    " doesn't have permission to create product."),
                    HttpStatus.FORBIDDEN);
        }
    }

    // ------------------- Update a Product ------------------------------------------------
    @RequestMapping(value = "/product/{product_id}/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProduct(@PathVariable("product_id") String product_id,
                                           @PathVariable("user_id") String user_id,
                                           @RequestBody ProductDTO productDTO) throws SQLException, ClassNotFoundException {
        // check data type
        long prodIdLong;
        long userIdLong;
        try {
            userIdLong = Long.parseLong(user_id);
            prodIdLong = Long.parseLong(product_id);
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
                logger.info("Updating Product with id {}", product_id);
                // find product_id from database, insert to DAO/Entity
                ProductDAO currentProduct = productRepo.findById(prodIdLong).orElse(null);
                if (currentProduct == null) {
                    logger.error("Unable to update. Product with id {} is not found.", product_id);
                    return new ResponseEntity<>(new CustomErrorType("Unable to update. Product with id "
                            + product_id + " is not found."), HttpStatus.NOT_FOUND);
                }
                ImageDAO imageDAO = imageRepo.findById(productDTO.getImage_id()).orElse(null);
                // map data from DTO to DAO
                currentProduct.setProduct_name(productDTO.getProduct_name());
                currentProduct.setProduct_model(productDTO.getProduct_model());
                currentProduct.setEstimated_cost(productDTO.getEstimated_cost());
                currentProduct.setImageDAO(imageDAO);
                // save mapped data
                productRepo.save(currentProduct);
                return new ResponseEntity<>(currentProduct, HttpStatus.OK);
            } else {
                logger.error("Unable to update product. User status is inactive.");
                return new ResponseEntity<>(new CustomErrorType("Unable to update. User status is inactive"),
                        HttpStatus.FORBIDDEN);
            }
        } else {
            logger.error("Unable to edit product. User role is not admin.");
            return new ResponseEntity<>(new CustomErrorType("Unable to edit. The role of " + currRole +
                    " doesn't have permission to edit product."), HttpStatus.FORBIDDEN);
        }
    }

    // ------------------- Delete a Product -----------------------------------------
    @RequestMapping(value = "/product/delete/{product_id}/{user_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> deleteProduct(@PathVariable("product_id") String product_id,
                                           @PathVariable("user_id") String user_id) throws SQLException, ClassNotFoundException {
        // check data type
        long prodIdLong;
        long userIdLong;
        try {
            userIdLong = Long.parseLong(user_id);
            prodIdLong = Long.parseLong(product_id);
        } catch (Exception e) {
            logger.error("Unable to delete. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to delete. ID must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        // check the role
        logger.info("Checking the role with user_id : {} before creating product", user_id);
        UserDAO currUser = userRepo.findById(userIdLong).orElse(null);
        String currRole = currUser.getRole();
        int currStatus = currUser.getStatus();

        if (currRole.equalsIgnoreCase("admin")) {
            if (currStatus == 1) {
                logger.info("Fetching & Deleting Product with id {}", prodIdLong);
                ProductDAO productDAO = productRepo.findById(prodIdLong).orElse(null);
                if (productDAO == null) {
                    logger.error("Unable to delete. Product with id {} not found.", prodIdLong);
                    return new ResponseEntity<>(new CustomErrorType("Unable to delete. Product with id "
                            + prodIdLong + " not found."), HttpStatus.NOT_FOUND);
                }
                productDAO.setProduct_status(0);
                productRepo.save(productDAO);
                Map<String, Object> response = new HashMap<>();
                response.put("message:", "Delete Product: Success!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.error("Unable to delete product. User status is inactive.");
                return new ResponseEntity<>(new CustomErrorType("Unable to delete. User status is inactive"),
                        HttpStatus.FORBIDDEN);
            }
        } else {
            logger.error("Unable to delete product. User role is not admin.");
            return new ResponseEntity<>(new CustomErrorType("Unable to delete. The role of " + currRole +
                    " doesn't have permission to delete product."), HttpStatus.FORBIDDEN);
        }
    }
}
