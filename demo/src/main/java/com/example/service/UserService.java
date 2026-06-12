package com.example.service;

import com.example.entity.User;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Integer id);
    User login(String username, String password);
    int insert(User user);
    int update(User user);
    int deleteById(Integer id);
}
