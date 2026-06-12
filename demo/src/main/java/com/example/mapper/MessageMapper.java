package com.example.mapper;

import com.example.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MessageMapper {
    List<Message> findAll();
    Message findById(Integer id);
    int insert(Message entity);
    int update(Message entity);
    int deleteById(Integer id);
}
