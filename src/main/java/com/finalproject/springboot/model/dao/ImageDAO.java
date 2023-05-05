package com.finalproject.springboot.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class ImageDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long image_id;
    @Column
    private String image_name;
    @Column(name = "image_path")
    private String image; // path
    @Column
    private int image_status;
    public ImageDAO() {
    }

    public ImageDAO(String image_name, String image, int image_status) {
        this.image_name = image_name;
        this.image = image;
        this.image_status = image_status;
    }

    public long getImage_id() {
        return image_id;
    }

    public void setImage_id(long image_id) {
        this.image_id = image_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImage_status() {
        return image_status;
    }

    public void setImage_status(int image_status) {
        this.image_status = image_status;
    }

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    @JsonIgnoreProperties("image")
    @JsonIgnore
    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
