package com.finalproject.springboot.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "payment")
public class PaymentDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long payment_id;
    @Column private String payment_date;
    @Column private String payment_method;
    @Column private double payment_amount;
    @Column private String payment_detail;
    @Column private String tf_acc_number;

    public PaymentDAO() {
    }

    public long getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(long payment_id) {
        this.payment_id = payment_id;
    }

    public String getPayment_date() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        payment_date = dateFormat.format(date);
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public double getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(double payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getPayment_detail() {
        return payment_detail;
    }

    public void setPayment_detail(String payment_detail) {
        this.payment_detail = payment_detail;
    }

    public String getTf_acc_number() {
        return tf_acc_number;
    }

    public void setTf_acc_number(String tf_acc_number) {
        this.tf_acc_number = tf_acc_number;
    }

    @ManyToOne
    @JoinColumn(name="order_id", referencedColumnName = "order_id")
    @JsonIgnoreProperties("payment")
    @JsonIgnore
    private OrderDAO orderDAO;

    public OrderDAO getOrderDAO() {
        return orderDAO;
    }

    public void setOrderDAO(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }
}
