package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping("/add")
    public Map<String, Object> insert(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            int r = userService.insert(user);
            result.put("code", r > 0 ? 200 : 500);
            result.put("msg", r > 0 ? "添加成功" : "添加失败");
        } catch (IllegalArgumentException e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            int r = userService.update(user);
            result.put("code", r > 0 ? 200 : 500);
            result.put("msg", r > 0 ? "更新成功" : "更新失败");
        } catch (IllegalArgumentException e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteById(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        int r = userService.deleteById(id);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "删除成功" : "删除失败");
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> resultMap = new HashMap<>();
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            resultMap.put("code", 200);
            resultMap.put("msg", "登录成功");
            resultMap.put("data", loginUser);
        } else {
            resultMap.put("code", 500);
            resultMap.put("msg", "用户名或密码错误");
        }
        return resultMap;
    }
}
