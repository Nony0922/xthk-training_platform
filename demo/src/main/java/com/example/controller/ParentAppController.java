package com.example.controller;

import com.example.entity.*;
import com.example.service.ParentAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app")
@CrossOrigin
public class ParentAppController {

    @Autowired
    private ParentAppService parentAppService;

    @GetMapping("/parent/by-user/{userId}")
    public Parent parentByUser(@PathVariable Integer userId) {
        return parentAppService.getProfileByUserId(userId);
    }

    @GetMapping("/announcements")
    public List<Announcement> announcements() {
        return parentAppService.getAnnouncements();
    }

    @GetMapping("/courses")
    public List<Course> courses() {
        return parentAppService.getCourses();
    }

    @GetMapping("/courses/{id}")
    public Course courseDetail(@PathVariable Integer id) {
        return parentAppService.getCourseDetail(id);
    }

    @GetMapping("/parent/{parentId}/students")
    public List<Student> students(@PathVariable Integer parentId) {
        return parentAppService.getStudents(parentId);
    }

    @GetMapping("/parent/{parentId}/schedules")
    public List<ClassSchedule> schedules(@PathVariable Integer parentId) {
        return parentAppService.getSchedules(parentId);
    }

    @GetMapping("/parent/{parentId}/attendance")
    public List<Attendance> attendance(@PathVariable Integer parentId) {
        return parentAppService.getAttendance(parentId);
    }

    @GetMapping("/parent/{parentId}/exams")
    public List<Exam> exams(@PathVariable Integer parentId) {
        return parentAppService.getExams(parentId);
    }

    @GetMapping("/parent/{parentId}/scores")
    public List<Score> scores(@PathVariable Integer parentId) {
        return parentAppService.getScores(parentId);
    }

    @GetMapping("/parent/{parentId}/messages")
    public List<Message> messages(@PathVariable Integer parentId) {
        return parentAppService.getMessages(parentId);
    }

    @PostMapping("/parent/message")
    public Map<String, Object> addMessage(@RequestBody Message message) {
        int r = parentAppService.addMessage(message);
        return Map.of("code", r > 0 ? 200 : 500, "msg", r > 0 ? "留言成功" : "留言失败");
    }

    @GetMapping("/parent/{parentId}/orders")
    public List<CourseOrder> orders(@PathVariable Integer parentId) {
        return parentAppService.getOrders(parentId);
    }

    @PostMapping("/parent/order")
    public Map<String, Object> createOrder(@RequestBody CourseOrder order) {
        return parentAppService.createOrder(order);
    }

    @PutMapping("/parent/order/{id}/pay")
    public Map<String, Object> payOrder(@PathVariable Integer id) {
        return parentAppService.payOrder(id);
    }

    @GetMapping("/parent/{parentId}/profile")
    public Parent profile(@PathVariable Integer parentId) {
        return parentAppService.getProfile(parentId);
    }

    @PutMapping("/parent/profile")
    public Map<String, Object> updateProfile(@RequestBody Parent parent) {
        int r = parentAppService.updateProfile(parent);
        return Map.of("code", r > 0 ? 200 : 500, "msg", r > 0 ? "保存成功" : "保存失败");
    }
}
