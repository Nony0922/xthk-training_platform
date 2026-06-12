package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAll();
    User findById(@Param("id") Integer id);
    User findByUsername(@Param("username") String username);
    User login(@Param("username") String username, @Param("password") String password);
    int insert(User user);
    int update(User user);
    int deleteById(@Param("id") Integer id);
}
