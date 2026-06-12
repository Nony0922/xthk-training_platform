package com.example.mapper;

import com.example.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface LeaveRequestMapper {
    List<LeaveRequest> findAll();
    LeaveRequest findById(Integer id);
    int insert(LeaveRequest entity);
    int update(LeaveRequest entity);
    int deleteById(Integer id);
}
