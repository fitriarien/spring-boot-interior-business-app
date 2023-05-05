package com.finalproject.springboot.model.dto;

public class ProductDTO {
    private long product_id;
    private String product_name;
    private String product_model;
    private double estimated_cost;
    private int product_status;
    private long image_id;

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_model() {
        return product_model;
    }

    public void setProduct_model(String product_model) {
        this.product_model = product_model;
    }

    public double getEstimated_cost() {
        return estimated_cost;
    }

    public void setEstimated_cost(double estimated_cost) {
        this.estimated_cost = estimated_cost;
    }

    public int getProduct_status() {
        return product_status;
    }

    public void setProduct_status(int product_status) {
        this.product_status = product_status;
    }

    public long getImage_id() {
        return image_id;
    }

    public void setImage_id(long image_id) {
        this.image_id = image_id;
    }
}
