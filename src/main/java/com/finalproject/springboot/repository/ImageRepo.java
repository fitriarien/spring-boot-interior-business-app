package com.finalproject.springboot.repository;

import com.finalproject.springboot.model.dao.ImageDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepo extends JpaRepository<ImageDAO, Long> {
}
