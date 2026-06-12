package com.example.service.impl;

import com.example.entity.Parent;
import com.example.mapper.ParentMapper;
import com.example.mapper.StudentMapper;
import com.example.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParentServiceImpl extends TeacherScopedServiceSupport implements ParentService {
    @Autowired
    private ParentMapper parentMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override public List<Parent> findAll() { return parentMapper.findAll(); }
    @Override
    public List<Parent> findAllForTeacher(Integer userId, Integer teacherLevel) {
        Set<Integer> parentIds = scopedParentIds(studentMapper.findAll(), userId, teacherLevel);
        if (parentIds.isEmpty()) {
            return List.of();
        }
        return parentMapper.findAll().stream()
                .filter(p -> parentIds.contains(p.getId()))
                .collect(Collectors.toList());
    }
    @Override public Parent findById(Integer id) { return parentMapper.findById(id); }
    @Override public Parent findByUserId(Integer userId) { return parentMapper.findByUserId(userId); }
    @Override public int insert(Parent entity) { return parentMapper.insert(entity); }
    @Override public int update(Parent entity) { return parentMapper.update(entity); }
    @Override public int deleteById(Integer id) { return parentMapper.deleteById(id); }
}
