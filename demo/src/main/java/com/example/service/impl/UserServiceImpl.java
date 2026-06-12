package com.example.service.impl;

import com.example.entity.Parent;
import com.example.entity.Teacher;
import com.example.entity.User;
import com.example.mapper.TeacherMapper;
import com.example.mapper.UserMapper;
import com.example.service.ParentService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ParentService parentService;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
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
        if (user != null && "teacher".equals(user.getRole())) {
            Teacher teacher = teacherMapper.findByUserId(user.getId());
            if (teacher != null) {
                user.setTeacherId(teacher.getId());
            }
        }
        return user;
    }

    @Override
    public int insert(User user) {
        validateUser(user, false);
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }
        normalizeTeacherLevel(user);
        int rows = userMapper.insert(user);
        if (rows > 0) {
            syncProfile(user);
        }
        return rows;
    }

    @Override
    public int update(User user) {
        validateUser(user, true);
        normalizeTeacherLevel(user);
        int rows = userMapper.update(user);
        if (rows > 0) {
            syncProfile(user);
        }
        return rows;
    }

    @Override
    public int deleteById(Integer id) {
        User user = userMapper.findById(id);
        if (user == null) {
            return 0;
        }
        if ("teacher".equals(user.getRole())) {
            Teacher teacher = teacherMapper.findByUserId(id);
            if (teacher != null) {
                teacherMapper.deleteById(teacher.getId());
            }
        } else if ("parent".equals(user.getRole())) {
            Parent parent = parentService.findByUserId(id);
            if (parent != null) {
                parentService.deleteById(parent.getId());
            }
        }
        return userMapper.deleteById(id);
    }

    private void validateUser(User user, boolean isUpdate) {
        if (user == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        if (!StringUtils.hasText(user.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (!StringUtils.hasText(user.getName())) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (!StringUtils.hasText(user.getRole())) {
            throw new IllegalArgumentException("角色不能为空");
        }
        if (!isUpdate && !StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }
        User existing = userMapper.findByUsername(user.getUsername());
        if (existing != null && (!isUpdate || !existing.getId().equals(user.getId()))) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if ("teacher".equals(user.getRole()) && user.getTeacherLevel() == null) {
            user.setTeacherLevel(1);
        }
    }

    private void normalizeTeacherLevel(User user) {
        if (!"teacher".equals(user.getRole())) {
            user.setTeacherLevel(null);
        }
    }

    private void syncProfile(User user) {
        if ("teacher".equals(user.getRole())) {
            Teacher teacher = teacherMapper.findByUserId(user.getId());
            if (teacher == null) {
                teacher = new Teacher();
                teacher.setUserId(user.getId());
                teacher.setName(user.getName());
                teacher.setPhone(user.getPhone());
                teacher.setEmail(user.getEmail());
                teacher.setTeacherLevel(user.getTeacherLevel() != null ? user.getTeacherLevel() : 1);
                teacher.setStatus(1);
                teacherMapper.insert(teacher);
            } else {
                teacher.setName(user.getName());
                teacher.setPhone(user.getPhone());
                teacher.setEmail(user.getEmail());
                teacher.setTeacherLevel(user.getTeacherLevel());
                teacherMapper.update(teacher);
            }
            return;
        }
        if ("parent".equals(user.getRole())) {
            Parent parent = parentService.findByUserId(user.getId());
            if (parent == null) {
                parent = new Parent();
                parent.setUserId(user.getId());
                parent.setName(user.getName());
                parent.setPhone(StringUtils.hasText(user.getPhone()) ? user.getPhone() : user.getUsername());
                parent.setEmail(user.getEmail());
                parentService.insert(parent);
            } else {
                parent.setName(user.getName());
                if (StringUtils.hasText(user.getPhone())) {
                    parent.setPhone(user.getPhone());
                }
                parent.setEmail(user.getEmail());
                parentService.update(parent);
            }
        }
    }
}
