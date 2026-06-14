package com.example.service;

import com.example.entity.*;
import java.util.List;
import java.util.Map;

public interface ParentAppService {
    List<Announcement> getAnnouncements();
    List<Course> getCourses();
    Course getCourseDetail(Integer id);
    List<Student> getStudents(Integer parentId);
    List<ClassSchedule> getSchedules(Integer parentId);
    List<Attendance> getAttendance(Integer parentId);
    List<Exam> getExams(Integer parentId);
    List<Score> getScores(Integer parentId);
    List<Message> getMessages(Integer parentId);
    List<CourseOrder> getOrders(Integer parentId);
    Parent getProfile(Integer parentId);
    Parent getProfileByUserId(Integer userId);
    int updateProfile(Parent parent);
    int addMessage(Message message);
    Map<String, Object> createOrder(CourseOrder order);
    Map<String, Object> payOrder(Integer orderId);
    Map<String, Object> cancelOrder(Integer parentId, Integer orderId);
    List<LeaveRequest> getLeaveRequests(Integer parentId);
    Map<String, Object> addLeaveRequest(Integer parentId, LeaveRequest leaveRequest);
    Map<String, Object> withdrawLeaveRequest(Integer parentId, Integer leaveId);

    List<HomeVisit> getHomeVisits(Integer parentId);

    List<LearningReport> getLearningReports(Integer parentId);
}
