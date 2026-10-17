package com.group17.course_service.controller;

import com.group17.course_service.util.CourseLogWriter;
import com.group17.course_service.model.Course;
import com.group17.course_service.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseLogWriter logWriter;

    public CourseController(CourseService courseService,
                        CourseLogWriter logWriter) {
    this.courseService = courseService;
    this.logWriter = logWriter;
    }
    
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.findAll();
    }

    @GetMapping("/{id}")
    public Course getCourse(@PathVariable int id) {
        return courseService.findById(id);
    }
@PostMapping
public Course create(@RequestBody Course c) {
    Course created = courseService.create(c);

    logWriter.log(
        "CREATE_COURSE id=" + created.getId() +
        " title=" + created.getTitle() +
        " capacity=" + created.getCapacity()
    );

    return created;
}


    @PutMapping("/{id}")
public Course update(@PathVariable int id, @RequestBody Course c) {
    Course updated = courseService.update(id, c);

    logWriter.log(
        "UPDATE_COURSE id=" + id +
        " title=" + updated.getTitle() +
        " capacity=" + updated.getCapacity()
    );

    return updated;
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable int id) {
    courseService.delete(id);

    logWriter.log("DELETE_COURSE id=" + id);
}


    @PutMapping("/{id}/enrolled")
    public Course updateEnrolled(@PathVariable int id, @RequestParam int delta) {
        return courseService.updateEnrolled(id, delta);
    }

    // ----- error handling -----
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFound(NoSuchElementException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public String handleConflict(IllegalStateException e) {
        return e.getMessage();
    }
    
}
