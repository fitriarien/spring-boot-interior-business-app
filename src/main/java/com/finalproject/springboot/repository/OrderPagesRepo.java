package com.finalproject.springboot.repository;

import com.finalproject.springboot.model.dao.OrderDAO;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPagesRepo extends PagingAndSortingRepository<OrderDAO, Long> {

}
