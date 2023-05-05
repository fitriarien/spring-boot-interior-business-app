package com.finalproject.springboot.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "order_details")
public class OrderDetDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long order_det_id;
    @Column private long estimated_time;
    @Column private long product_size;
    @Column private String product_theme;
    @Column private double subtotal;

    public OrderDetDAO(long estimated_time, long product_size, String product_theme, double subtotal) {
        this.estimated_time = estimated_time;
        this.product_size = product_size;
        this.product_theme = product_theme;
        this.subtotal = subtotal;
    }

    public long getOrder_det_id() {
        return order_det_id;
    }

    public void setOrder_det_id(long order_det_id) {
        this.order_det_id = order_det_id;
    }

    public long getEstimated_time() {
        return estimated_time;
    }

    public void setEstimated_time(long estimated_time) {
        this.estimated_time = estimated_time;
    }

    public long getProduct_size() {
        return product_size;
    }

    public void setProduct_size(long product_size) {
        this.product_size = product_size;
    }

    public String getProduct_theme() {
        return product_theme;
    }

    public void setProduct_theme(String product_theme) {
        this.product_theme = product_theme;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public OrderDetDAO() {
    }

    @ManyToOne
    @JoinColumn(name="order_id", referencedColumnName = "order_id")
    @JsonIgnoreProperties("order_details")
    private OrderDAO orderDAO;

    public void setOrderDAO(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @ManyToOne
    @JoinColumn(name="product_id", referencedColumnName = "product_id")
    @JsonIgnoreProperties("order_details")
    private ProductDAO productDAO;

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
}
