package com.finalproject.springboot.model.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderDTO {
    private long order_id;
    private String order_code;
    private String order_date;
    private String visit_date;
    private String visit_time;
    private String visit_address;
    private double order_amount;

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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        order_date = dateFormat.format(date);
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
}
