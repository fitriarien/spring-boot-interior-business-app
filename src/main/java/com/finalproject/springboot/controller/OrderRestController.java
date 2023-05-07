package com.finalproject.springboot.controller;

import com.finalproject.springboot.model.dao.OrderDAO;
import com.finalproject.springboot.model.dao.OrderDetDAO;
import com.finalproject.springboot.model.dao.ProductDAO;
import com.finalproject.springboot.model.dao.UserDAO;
import com.finalproject.springboot.model.dto.OrderDTO;
import com.finalproject.springboot.repository.OrderPagesRepo;
import com.finalproject.springboot.repository.OrderRepo;
import com.finalproject.springboot.repository.ProductRepo;
import com.finalproject.springboot.repository.UserRepo;
import com.finalproject.springboot.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class OrderRestController {
    public static final Logger logger = LoggerFactory.getLogger(OrderRestController.class);
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    OrderPagesRepo orderPagesRepo;

    // -------------------------- View User Orders -----------------------------------------
    @RequestMapping(value = "/order/{user_id}", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<?> viewAllOrder(@PathVariable("user_id") String user_id) throws SQLException, ClassNotFoundException {
        // data type exception handler
        long userIdLong;
        try {
            userIdLong = Long.parseLong(user_id);
        } catch (Exception e) {
            logger.error("Unable to view. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to view. User id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        // check the status
        logger.info("Checking the status with user_id : {} before viewing order", user_id);
        UserDAO currUser = userRepo.findById(userIdLong).orElse(null);
        int currStatus = currUser.getStatus();

        if (currStatus==1) {
            List<OrderDAO> orderDAOList = orderRepo.findByUserDAOId(userIdLong); //direct jpa method
            if (orderDAOList.isEmpty()) {
                return new ResponseEntity<>(orderDAOList, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(orderDAOList, HttpStatus.OK);
        } else {
            logger.error("Unable to view order. User status is inactive.");
            return new ResponseEntity<>(new CustomErrorType("Unable to view order. User status is inactive"),
                    HttpStatus.FORBIDDEN);
        }
    }

    // -------------------------- View an Order -----------------------------------------
    @RequestMapping(value = "/order/{order_id}/{user_id}", method = RequestMethod.GET)
    public ResponseEntity<?> viewOrder(@PathVariable("user_id") String user_id,
                                       @PathVariable("order_id") String order_id) throws SQLException, ClassNotFoundException {
        // data type exception handler
        long userIdLong;
        long orderIdLong;
        try {
            userIdLong = Long.parseLong(user_id);
            orderIdLong =Long.parseLong(order_id);
        } catch (Exception e) {
            logger.error("Unable to view. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to view. ID must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        // check the status
        logger.info("Checking the status with user_id : {} before viewing order", user_id);
        UserDAO currUser = userRepo.findById(userIdLong).orElse(null);
        int currStatus = currUser.getStatus();

        if (currStatus==1) {
            logger.info("Fetching Order with id {}", order_id);
            OrderDAO currOrder = orderRepo.findById(orderIdLong).orElse(null);
            if (currOrder == null) {
                logger.error("Order with id {} not found.", order_id);
                return new ResponseEntity<>(new CustomErrorType("Order with id " + order_id  + " not found"),
                        HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(currOrder, HttpStatus.OK);
        } else {
            logger.error("Unable to view order. User status is inactive.");
            return new ResponseEntity<>(new CustomErrorType("Unable to view order. User status is inactive"),
                    HttpStatus.FORBIDDEN);
        }
    }

    // ----------------------- Create Visit Order -------------------------------------------
    @RequestMapping(value = "/order/{user_id}", method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<?> createOrder(@PathVariable("user_id") String user_id,
                                         @RequestBody OrderDTO orderDTO) throws SQLException, ClassNotFoundException {
        // data type exception handler
        long userIdLong;
        try {
            logger.info("Checking the id type with user_id : {} before creating order", user_id);
            userIdLong = Long.parseLong(user_id);
        } catch (Exception e) {
            logger.error("Unable to order. Wrong data type of ID");
            return new ResponseEntity<>(new CustomErrorType("Unable to order. User id must be a number."),
                    HttpStatus.FORBIDDEN);
        }

        // check role & status
        logger.info("Checking the role with user_id : {} before creating order", user_id);
        UserDAO currUser = userRepo.findById(userIdLong).orElse(null);
        String currRole = currUser.getRole();
        int currStatus = currUser.getStatus();

        if (currRole.equalsIgnoreCase("customer")) {
            if (currStatus==1) {
                OrderDAO orderDAO = new OrderDAO();
                orderDAO.setUserDAO(currUser); // set user_id
                orderDAO.setOrder_date(orderDTO.getOrder_date());
                orderDAO.setVisit_date(orderDTO.getVisit_date());
                orderDAO.setVisit_time(orderDTO.getVisit_time());
                orderDAO.setVisit_address(orderDTO.getVisit_address());
                logger.info("Save order");
                orderRepo.save(orderDAO);

                // construct order_code
                logger.info("Get order id");
                long maxOrderId = orderRepo.getMaxOrder_id();
                orderDAO.setOrder_code("TR"+maxOrderId);
                orderRepo.save(orderDAO);
                return new ResponseEntity<>(orderDAO, HttpStatus.CREATED);
            } else {
                logger.error("Unable to order. User status is inactive.");
                return new ResponseEntity<>(new CustomErrorType("Unable to order. User status is inactive"),
                        HttpStatus.FORBIDDEN);
            }
        } else {
            logger.error("Unable to order. User role is not customer.");
            return new ResponseEntity<>(new CustomErrorType("Unable to make order. The role of " + currRole +
                    " doesn't have permission to order."), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // ------------------- Update Order and Add Details by Admin -------------------------------------------
    @RequestMapping(value = "/order/{order_id}/{user_id}", method = RequestMethod.PUT, produces="application/json")
    public ResponseEntity<?> updateOrder(@PathVariable("order_id") String order_id,
                                         @PathVariable("user_id") String user_id,
                                         @RequestBody Map<String, Object> payload) throws SQLException, ClassNotFoundException {
        // check data type
        long userIdLong;
        long orderIdLong;
        try {
            userIdLong = Long.parseLong(user_id);
            orderIdLong =Long.parseLong(order_id);
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
            if (currStatus==1) {
                // find order by id
                OrderDAO currOrder = orderRepo.findById(orderIdLong).orElse(null);

                double orderAmount = currOrder.getOrder_amount();

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> orderDet = (List<Map<String, Object>>) payload.get("order_details");

                for (Map<String, Object> orderDetObj : orderDet) {
                    logger.info("Adding Detail...");

                    OrderDetDAO orderDetDAO = new OrderDetDAO();
                    // get product_id
                    long product_id = Long.parseLong(String.valueOf(orderDetObj.get("product_id")));
                    // logger.info("step 1");
                    ProductDAO currProduct = productRepo.findById(product_id).orElse(null);
                    // set product_id to order_detail
                    orderDetDAO.setProductDAO(currProduct);
                    // set other field to order_detail
                    // logger.info("step 2");
                    orderDetDAO.setEstimated_time(Long.parseLong(String.valueOf(orderDetObj.get("estimated_time"))));
                    orderDetDAO.setProduct_size(Long.parseLong(String.valueOf(orderDetObj.get("product_size"))));
                    orderDetDAO.setProduct_theme(orderDetObj.get("product_theme").toString());
                    orderDetDAO.setSubtotal(Double.parseDouble(String.valueOf(orderDetObj.get("product_cost"))));
                    // add subtotal to order_amount
                    orderAmount += orderDetDAO.getSubtotal();

                    // update
                    currOrder.updateOrderDet(orderDetDAO);
                }

                // add order_amount
                currOrder.setOrder_amount(orderAmount);
                orderRepo.save(currOrder);
                return new ResponseEntity<>(currOrder, HttpStatus.CREATED);
            } else {
                logger.error("Unable to order. User status is inactive.");
                return new ResponseEntity<>(new CustomErrorType("Unable to order. User status is inactive"),
                        HttpStatus.FORBIDDEN);
            }
        } else {
            logger.error("Unable to update order detail. User role is not admin.");
            return new ResponseEntity<>(new CustomErrorType("Unable to update. The role of " + currRole +
                    " doesn't have permission to update order details."), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // ------------------- Retrieve all orders -------------------------------------------
    @RequestMapping(value = "/order/", method = RequestMethod.GET, produces="application/json")
    public ResponseEntity<List<OrderDAO>> getAllOrders() throws SQLException, ClassNotFoundException {
        logger.info("Step 1");
        List<OrderDAO> orders = orderRepo.findAll(); //direct jpa method
        if (orders.isEmpty()) {
            logger.error("Step error");
            return new ResponseEntity<>(orders, HttpStatus.NOT_FOUND);
        }
        logger.info("Step 2");
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // ------------------- Retrieve all orders with pagination -------------------------------------------
    @GetMapping(value = "/orders")
    public Page<OrderDAO> findAll(@RequestParam int page, @RequestParam int size) {
        logger.info("Request orders page : {}", page);
        PageRequest pageRequest = PageRequest.of(page, size);
        return orderPagesRepo.findAll(pageRequest);
    }
}
