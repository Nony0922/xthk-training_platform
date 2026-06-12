package com.example.service.impl;

import com.example.entity.Parent;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.ParentService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ParentService parentService;

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.login(username, password);
        if (user != null && "parent".equals(user.getRole())) {
            if (user.getParentId() == null) {
                Parent parent = parentService.findByUserId(user.getId());
                if (parent == null) {
                    parent = new Parent();
                    parent.setUserId(user.getId());
                    parent.setName(user.getName());
                    parent.setPhone(user.getPhone() != null ? user.getPhone() : user.getUsername());
                    parentService.insert(parent);
                    parent = parentService.findByUserId(user.getId());
                }
                if (parent != null) {
                    user.setParentId(parent.getId());
                }
            }
        }
        return user;
    }
}
