package com.example.service.impl;

import com.example.entity.*;
import com.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParentAppServiceImpl implements ParentAppService {

    @Autowired private AnnouncementService announcementService;
    @Autowired private CourseService courseService;
    @Autowired private StudentService studentService;
    @Autowired private ClassScheduleService classScheduleService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private ExamService examService;
    @Autowired private ScoreService scoreService;
    @Autowired private MessageService messageService;
    @Autowired private CourseOrderService courseOrderService;
    @Autowired private ParentService parentService;
    @Autowired private LeaveRequestService leaveRequestService;
    @Autowired private LearningReportService learningReportService;

    private List<Student> studentsOf(Integer parentId) {
        return studentService.findAll().stream()
                .filter(s -> parentId.equals(s.getParentId()))
                .collect(Collectors.toList());
    }

    private Set<Integer> classIdsOf(Integer parentId) {
        return studentsOf(parentId).stream()
                .map(Student::getClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<Integer> studentIdsOf(Integer parentId) {
        return studentsOf(parentId).stream()
                .map(Student::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Announcement> getAnnouncements() {
        return announcementService.findAll().stream()
                .filter(a -> a.getStatus() != null && a.getStatus() == 1)
                .filter(a -> "all".equals(a.getTargetRole()) || "parent".equals(a.getTargetRole()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> getCourses() {
        return courseService.findAll().stream()
                .filter(c -> c.getStatus() != null && c.getStatus() == 1)
                .collect(Collectors.toList());
    }

    @Override
    public Course getCourseDetail(Integer id) {
        return courseService.findById(id);
    }

    @Override
    public List<Student> getStudents(Integer parentId) {
        return studentsOf(parentId);
    }

    @Override
    public List<ClassSchedule> getSchedules(Integer parentId) {
        Set<Integer> classIds = classIdsOf(parentId);
        return classScheduleService.findAll().stream()
                .filter(s -> classIds.contains(s.getClassId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> getAttendance(Integer parentId) {
        Set<Integer> studentIds = studentIdsOf(parentId);
        return attendanceService.findAll().stream()
                .filter(a -> studentIds.contains(a.getStudentId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Exam> getExams(Integer parentId) {
        Set<Integer> classIds = classIdsOf(parentId);
        return examService.findAll().stream()
                .filter(e -> classIds.contains(e.getClassId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Score> getScores(Integer parentId) {
        Set<Integer> studentIds = studentIdsOf(parentId);
        return scoreService.findAll().stream()
                .filter(s -> studentIds.contains(s.getStudentId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessages(Integer parentId) {
        return messageService.findAll().stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseOrder> getOrders(Integer parentId) {
        return courseOrderService.findAll().stream()
                .filter(o -> parentId.equals(o.getParentId()))
                .collect(Collectors.toList());
    }

    @Override
    public Parent getProfile(Integer parentId) {
        return parentService.findById(parentId);
    }

    @Override
    public Parent getProfileByUserId(Integer userId) {
        return parentService.findByUserId(userId);
    }

    @Override
    public int updateProfile(Parent parent) {
        return parentService.update(parent);
    }

    @Override
    public int addMessage(Message message) {
        message.setStatus(0);
        return messageService.insert(message);
    }

    @Override
    public Map<String, Object> createOrder(CourseOrder order) {
        Map<String, Object> result = new HashMap<>();
        Course course = courseService.findById(order.getCourseId());
        String err = courseService.validatePurchasable(course);
        if (err != null) {
            result.put("code", 500);
            result.put("msg", err);
            return result;
        }
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            order.setOrderNo("ORD" + System.currentTimeMillis());
        }
        if (order.getStatus() == null) {
            order.setStatus(0);
        }
        if (course != null) {
            if (order.getCourseName() == null) order.setCourseName(course.getName());
            if (order.getTeacherName() == null) order.setTeacherName(course.getTeacherName());
            if (order.getHours() == null) order.setHours(course.getHours());
            if (order.getFee() == null) order.setFee(course.getFee());
        }
        int r = courseOrderService.insert(order);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "订单创建成功" : "订单创建失败");
        result.put("orderNo", order.getOrderNo());
        result.put("orderId", order.getId());
        return result;
    }

    @Override
    public List<LeaveRequest> getLeaveRequests(Integer parentId) {
        Set<Integer> studentIds = studentIdsOf(parentId);
        if (studentIds.isEmpty()) {
            return List.of();
        }
        return leaveRequestService.findAll().stream()
                .filter(l -> studentIds.contains(l.getStudentId()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> addLeaveRequest(Integer parentId, LeaveRequest leaveRequest) {
        Map<String, Object> result = new HashMap<>();
        Parent parent = parentService.findById(parentId);
        if (parent == null) {
            result.put("code", 500);
            result.put("msg", "家长信息不存在");
            return result;
        }
        Set<Integer> studentIds = studentIdsOf(parentId);
        if (leaveRequest.getStudentId() == null || !studentIds.contains(leaveRequest.getStudentId())) {
            result.put("code", 500);
            result.put("msg", "只能为本人的孩子申请请假");
            return result;
        }
        leaveRequest.setApplicantId(parent.getUserId());
        leaveRequest.setApplicantName(parent.getName());
        leaveRequest.setStatus(0);
        int r = leaveRequestService.insert(leaveRequest);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "请假申请已提交" : "提交失败");
        return result;
    }

    @Override
    public Map<String, Object> withdrawLeaveRequest(Integer parentId, Integer leaveId) {
        Map<String, Object> result = new HashMap<>();
        LeaveRequest leave = leaveRequestService.findById(leaveId);
        if (leave == null) {
            result.put("code", 500);
            result.put("msg", "请假记录不存在");
            return result;
        }
        Set<Integer> studentIds = studentIdsOf(parentId);
        if (!studentIds.contains(leave.getStudentId())) {
            result.put("code", 500);
            result.put("msg", "无权操作该请假记录");
            return result;
        }
        if (leave.getStatus() == null || leave.getStatus() != 0) {
            result.put("code", 500);
            result.put("msg", "仅待审批的请假可以撤回");
            return result;
        }
        leave.setStatus(3);
        int r = leaveRequestService.update(leave);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "撤回成功" : "撤回失败");
        return result;
    }

    @Override
    public Map<String, Object> payOrder(Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        CourseOrder order = courseOrderService.findById(orderId);
        if (order == null) {
            result.put("code", 500);
            result.put("msg", "订单不存在");
            return result;
        }
        if (order.getStatus() != null && order.getStatus() == 1) {
            result.put("code", 500);
            result.put("msg", "订单已支付");
            return result;
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            result.put("code", 500);
            result.put("msg", "订单状态不可支付");
            return result;
        }
        Course course = courseService.findById(order.getCourseId());
        String err = courseService.validatePurchasable(course);
        if (err != null) {
            result.put("code", 500);
            result.put("msg", err);
            return result;
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        int r = courseOrderService.update(order);
        if (r > 0) {
            courseService.incrementEnrolledCount(order.getCourseId());
        }
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "支付成功" : "支付失败");
        return result;
    }

    @Override
    public Map<String, Object> cancelOrder(Integer parentId, Integer orderId) {
        Map<String, Object> result = new HashMap<>();
        CourseOrder order = courseOrderService.findById(orderId);
        if (order == null) {
            result.put("code", 500);
            result.put("msg", "订单不存在");
            return result;
        }
        if (!parentId.equals(order.getParentId())) {
            result.put("code", 500);
            result.put("msg", "无权操作该订单");
            return result;
        }
        if (order.getStatus() == null || order.getStatus() != 0) {
            result.put("code", 500);
            result.put("msg", "仅待支付订单可以取消");
            return result;
        }
        order.setStatus(2);
        int r = courseOrderService.update(order);
        result.put("code", r > 0 ? 200 : 500);
        result.put("msg", r > 0 ? "订单已取消" : "取消失败");
        return result;
    }

    @Override
    public List<LearningReport> getLearningReports(Integer parentId) {
        return learningReportService.listForParent(parentId);
    }
}
