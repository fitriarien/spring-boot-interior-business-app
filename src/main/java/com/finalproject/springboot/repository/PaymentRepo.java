package com.finalproject.springboot.repository;

import com.finalproject.springboot.model.dao.PaymentDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<PaymentDAO, Long> {
}
