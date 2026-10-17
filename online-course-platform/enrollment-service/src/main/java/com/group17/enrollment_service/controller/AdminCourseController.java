package com.group17.enrollment_service.controller;

import com.group17.enrollment_service.client.CourseClient;
import com.group17.enrollment_service.model.CourseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Teacher/Admin course management endpoints.
 *
 * Note: Demo 作業版本，未做真正的帳密驗證；前端登入頁面會導向 teacher.html 才會使用這些 API。
 */
@RestController
@RequestMapping("/admin/courses")
public class AdminCourseController {

    private final CourseClient courseClient;

    public AdminCourseController(CourseClient courseClient) {
        this.courseClient = courseClient;
    }

    @GetMapping
    public List<CourseDto> list() {
        return courseClient.getAllCourses();
    }

    @GetMapping("/{id}")
    public CourseDto get(@PathVariable int id) {
        return courseClient.getCourse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDto create(@RequestBody CourseDto dto) {
        return courseClient.createCourse(dto);
    }

    @PutMapping("/{id}")
    public CourseDto update(@PathVariable int id, @RequestBody CourseDto dto) {
        return courseClient.updateCourse(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        courseClient.deleteCourse(id);
    }
}
