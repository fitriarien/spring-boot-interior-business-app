package com.finalproject.springboot.repository;

import com.finalproject.springboot.model.dao.UserDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<UserDAO, Long> {
    UserDAO findByUsername(String username);
}