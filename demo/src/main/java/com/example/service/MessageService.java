package com.example.service;

import com.example.entity.Message;
import java.util.List;

public interface MessageService {
    List<Message> findAll();
    Message findById(Integer id);
    int insert(Message entity);
    int update(Message entity);
    int deleteById(Integer id);
}
