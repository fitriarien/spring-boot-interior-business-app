package com.finalproject.springboot.model.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class UserDAO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	private String username;
	@Column
	@JsonIgnore
	private String password;
	@Column private String name;
	@Column private String email;
	@Column private String contact;
	@Column private String address;
	@Column private String role;
	@Column private int status;

	public UserDAO() {
	}

	public UserDAO(String username, String password, String name, String email, String contact, String address,
				   String role, int status) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
		this.contact = contact;
		this.address = address;
		this.role = role;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@OneToMany(mappedBy = "userDAO", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("user")
	@JsonIgnore
	List<OrderDAO> orderDAOList = new ArrayList<>();

	public List<OrderDAO> getOrderDAOList() {
		return orderDAOList;
	}

	public void setOrderDAOList(List<OrderDAO> orderDAOList) {
		this.orderDAOList = orderDAOList;
	}

	@OneToMany(mappedBy = "userDAO", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("user")
	@JsonIgnore
	List<ProductDAO> productDAOList = new ArrayList<>();

	public List<ProductDAO> getProductDAOList() {
		return productDAOList;
	}

	public void setProductDAOList(List<ProductDAO> productDAOList) {
		this.productDAOList = productDAOList;
	}

	@OneToMany(mappedBy = "userDAO", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("user")
	@JsonIgnore
	List<ImageDAO> imageDAOList = new ArrayList<>();

	public List<ImageDAO> getImageDAOList() {
		return imageDAOList;
	}

	public void setImageDAOList(List<ImageDAO> imageDAOList) {
		this.imageDAOList = imageDAOList;
	}
}