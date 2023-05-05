package com.finalproject.springboot.controller;

import com.finalproject.springboot.model.dao.OrderDAO;
import com.finalproject.springboot.model.dao.PaymentDAO;
import com.finalproject.springboot.model.dao.UserDAO;
import com.finalproject.springboot.model.dto.PaymentDTO;
import com.finalproject.springboot.repository.OrderRepo;
import com.finalproject.springboot.repository.PaymentRepo;
import com.finalproject.springboot.repository.UserRepo;
import com.finalproject.springboot.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class PaymentRestController {
    public static final Logger logger = LoggerFactory.getLogger(PaymentRestController.class);
    @Autowired
    UserRepo userRepo;
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    PaymentRepo paymentRepo;

    // ------------------- Create Payment -------------------------------------------
    @RequestMapping(value = "/payment/{order_id}/{user_id}", method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<?> createPayment(@PathVariable("order_id") String order_id,
                                           @PathVariable("user_id") String user_id,
                                           @RequestBody PaymentDTO paymentDTO) throws SQLException, ClassNotFoundException {
        // check data type
        long orderIdLong;
        long userIdLong;
        try {
            orderIdLong = Long.parseLong(order_id);
            userIdLong = Long.parseLong(user_id);
        } catch (Exception e) {
            logger.error("Unable to update. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to update. ID must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        // check the role
        logger.info("Checking the role with user_id : {} before creating payment", user_id);
        UserDAO currUser = userRepo.findById(userIdLong).orElse(null);
        String currRole = currUser.getRole();
        int currStatus = currUser.getStatus();

        PaymentDAO currPayment = new PaymentDAO();
        if (currRole.equalsIgnoreCase("customer")) {
            if (currStatus == 1) {
                // get order_id
                OrderDAO currOrder = orderRepo.findById(orderIdLong).orElse(null);
                if (currOrder == null) {
                    logger.error("Unable to create payment. Order with id {} is not found.", orderIdLong);
                    return new ResponseEntity<>(new CustomErrorType("Unable to create payment. Order with id "
                            + orderIdLong + " is not found."), HttpStatus.NOT_FOUND);
                }

                currPayment.setOrderDAO(currOrder); // set order_id
                currPayment.setPayment_date(currPayment.getPayment_date());
                currPayment.setPayment_method(paymentDTO.getPayment_method());
                currPayment.setPayment_amount(paymentDTO.getPayment_amount());
                currPayment.setPayment_detail(paymentDTO.getPayment_detail());
                currPayment.setTf_acc_number(paymentDTO.getTf_acc_number());

                paymentRepo.save(currPayment);
                return new ResponseEntity<>(currPayment, HttpStatus.CREATED);
            } else {
                logger.error("Unable to create payment. User status is inactive.");
                return new ResponseEntity<>(new CustomErrorType("Unable to create payment. User status is inactive"),
                        HttpStatus.FORBIDDEN);
            }
        } else {
            logger.error("Unable to create payment. User role is not customer.");
            return new ResponseEntity<>(new CustomErrorType("Unable to create payment. The role of " + currRole +
                    " doesn't have permission to create payment."), HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
