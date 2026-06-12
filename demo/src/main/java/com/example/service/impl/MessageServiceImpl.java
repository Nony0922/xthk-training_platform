package com.example.service.impl;

import com.example.entity.Message;
import com.example.mapper.MessageMapper;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override public List<Message> findAll() { return messageMapper.findAll(); }
    @Override public Message findById(Integer id) { return messageMapper.findById(id); }
    @Override public int insert(Message entity) { return messageMapper.insert(entity); }
    @Override public int update(Message entity) { return messageMapper.update(entity); }
    @Override public int deleteById(Integer id) { return messageMapper.deleteById(id); }
}
