package com.finalproject.springboot.repository;

import com.finalproject.springboot.model.dao.ProductDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductDAO, Long> {
}
