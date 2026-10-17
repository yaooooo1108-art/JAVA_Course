package com.group17.enrollment_service.controller;

import com.group17.enrollment_service.client.CourseClient;
import com.group17.enrollment_service.model.CourseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final CourseClient courseClient;

    public DebugController(CourseClient courseClient) {
        this.courseClient = courseClient;
    }

    @GetMapping("/course/{id}")
    public CourseDto debugGetCourse(@PathVariable int id) {
        return courseClient.getCourse(id);
    }
}
