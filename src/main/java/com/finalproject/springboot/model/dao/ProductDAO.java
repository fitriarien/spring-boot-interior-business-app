package com.finalproject.springboot.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class ProductDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long product_id;
    @Column
    private String product_name;
    @Column
    private String product_model;
    @Column
    private double estimated_cost;
    @Column
    private int product_status;

    public ProductDAO() {
    }

    public ProductDAO(String product_name, String product_model, double estimated_cost, int product_status) {
        this.product_name = product_name;
        this.product_model = product_model;
        this.estimated_cost = estimated_cost;
        this.product_status = product_status;
    }

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "image_id")
    @JsonIgnoreProperties("product")
    private ImageDAO imageDAO;

    public ImageDAO getImageDAO() {
        return imageDAO;
    }

    public void setImageDAO(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    @OneToMany(mappedBy = "productDAO", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("product")
    @JsonIgnore
    List<OrderDetDAO> orderDetDAOList = new ArrayList<>();

    public List<OrderDetDAO> getOrderDetDAOList() {
        return orderDetDAOList;
    }

    public void setOrderDetDAOList(List<OrderDetDAO> orderDetDAOList) {
        this.orderDetDAOList = orderDetDAOList;
    }

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    @JsonIgnoreProperties("product")
    @JsonIgnore
    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
