package com.group17.enrollment_service.controller;

import com.group17.enrollment_service.model.CourseDto;
import com.group17.enrollment_service.model.EnrollmentRecord;
import com.group17.enrollment_service.service.EnrollmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enroll")
    public CourseDto enroll(@RequestParam String studentId, @RequestParam int courseId) {
        return enrollmentService.enroll(studentId, courseId);
    }

    @PostMapping("/drop")
    public CourseDto drop(@RequestParam String studentId, @RequestParam int courseId) {
        return enrollmentService.drop(studentId, courseId);
    }

    @GetMapping("/enrollments")
    public List<Integer> list(@RequestParam String studentId) {
        return enrollmentService.listCourseIds(studentId);
    }

    @GetMapping("/enrollments/detail")
    public List<CourseDto> listDetail(@RequestParam String studentId) {
        return enrollmentService.listCourseDetails(studentId);
    }

    @GetMapping("/courses")
    public List<CourseDto> allCourses() {
        return enrollmentService.listAllCourses();
    }

    // 管理/報表用：查全部選課紀錄（方便 demo / 報告 / report service）
    @GetMapping("/admin/enrollments")
    public List<EnrollmentRecord> listAllEnrollments() {
        return enrollmentService.listAllEnrollments();
    }
}
