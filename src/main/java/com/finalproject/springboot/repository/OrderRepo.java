package com.finalproject.springboot.repository;

import com.finalproject.springboot.model.dao.OrderDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<OrderDAO, Long> {
    List<OrderDAO> findByUserDAOId(long user_id);
    @Query("SELECT MAX(od.order_id) FROM OrderDAO od")
    long getMaxOrder_id();
}
