package com.finalproject.springboot.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`order`")
public class OrderDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long order_id;
    @Column private String order_code;
    @Column private String order_date;
    @Column private String visit_date;
    @Column private String visit_time;
    @Column private String visit_address;
    @Column private double order_amount;

    public OrderDAO() {
    }

    public OrderDAO(String order_code, String order_date, String visit_date, String visit_time,
                    String visit_address, double order_amount) {
        this.order_code = order_code;
        this.order_date = order_date;
        this.visit_date = visit_date;
        this.visit_time = visit_time;
        this.visit_address = visit_address;
        this.order_amount = order_amount;
    }

    public long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(long order_id) {
        this.order_id = order_id;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getVisit_time() {
        return visit_time;
    }

    public void setVisit_time(String visit_time) {
        this.visit_time = visit_time;
    }

    public String getVisit_address() {
        return visit_address;
    }

    public void setVisit_address(String visit_address) {
        this.visit_address = visit_address;
    }

    public double getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(double order_amount) {
        this.order_amount = order_amount;
    }

    @OneToMany(mappedBy = "orderDAO", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("`order`")
    List<OrderDetDAO> orderDetDAOList = new ArrayList<>();

    public void updateOrderDet(OrderDetDAO orderDetDAO) {
        orderDetDAOList.add(orderDetDAO);
        orderDetDAO.setOrderDAO(this);
    }

    public List<OrderDetDAO> getOrderDetDAOList() {
        return orderDetDAOList;
    }

    public void setOrderDetDAOList(List<OrderDetDAO> orderDetDAOList) {
        this.orderDetDAOList = orderDetDAOList;
    }

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    @JsonIgnoreProperties("`order`")
    @JsonIgnore
    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @OneToMany(mappedBy = "orderDAO", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("`order`")
    List<PaymentDAO> paymentDAOList = new ArrayList<>();

    public void addPayment(PaymentDAO paymentDAO) {
        paymentDAOList.add(paymentDAO);
        paymentDAO.setOrderDAO(this);
    }
    public List<PaymentDAO> getPaymentDAOList() {
        return paymentDAOList;
    }

    public void setPaymentDAOList(List<PaymentDAO> paymentDAOList) {
        this.paymentDAOList = paymentDAOList;
    }

}
