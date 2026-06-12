package com.example.service.impl;

import com.example.entity.Score;
import com.example.mapper.ScoreMapper;
import com.example.mapper.StudentMapper;
import com.example.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl extends TeacherScopedServiceSupport implements ScoreService {
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override public List<Score> findAll() { return scoreMapper.findAll(); }
    @Override
    public List<Score> findAllForTeacher(Integer userId, Integer teacherLevel) {
        Set<Integer> studentIds = scopedStudentIds(studentMapper.findAll(), userId, teacherLevel);
        if (studentIds.isEmpty()) {
            return List.of();
        }
        return scoreMapper.findAll().stream()
                .filter(item -> studentIds.contains(item.getStudentId()))
                .collect(Collectors.toList());
    }
    @Override public Score findById(Integer id) { return scoreMapper.findById(id); }
    @Override public int insert(Score entity) { return scoreMapper.insert(entity); }
    @Override public int update(Score entity) { return scoreMapper.update(entity); }
    @Override public int deleteById(Integer id) { return scoreMapper.deleteById(id); }
}
