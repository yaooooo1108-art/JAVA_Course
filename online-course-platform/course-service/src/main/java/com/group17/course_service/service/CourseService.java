package com.group17.course_service.service;

import com.group17.course_service.model.Course;
import com.group17.course_service.storage.CourseFileStore;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {

    private final Map<Integer, Course> store = new LinkedHashMap<>();
    private final CourseFileStore fileStore;
    private int nextId = 1;

    public CourseService(CourseFileStore fileStore) {
        this.fileStore = fileStore;
    }

    @PostConstruct
    public void init() {
        Map<Integer, Course> loaded = fileStore.load();

        if (loaded.isEmpty()) {
            store.put(1, new Course(1, "Java Programming", 60, 0));
            store.put(2, new Course(2, "Data Structures", 50, 0));
            store.put(3, new Course(3, "Database Systems", 40, 0));
            nextId = 4;
            fileStore.save(store.values());
        } else {
            store.putAll(loaded);
            nextId = store.keySet().stream().max(Integer::compare).orElse(0) + 1;
        }
    }

    public List<Course> findAll() {
        return new ArrayList<>(store.values());
    }

    public Course findById(int id) {
        Course c = store.get(id);
        if (c == null) throw new NoSuchElementException("Course not found");
        return c;
    }

    public Course create(Course c) {
        validate(c);
        c.setId(nextId++);
        c.setEnrolled(0);
        store.put(c.getId(), c);
        fileStore.save(store.values());
        return c;
    }

    public Course update(int id, Course c) {
        Course old = findById(id);
        validate(c);

        if (c.getCapacity() < old.getEnrolled()) {
            throw new IllegalStateException("capacity < enrolled");
        }

        old.setTitle(c.getTitle());
        old.setCapacity(c.getCapacity());
        fileStore.save(store.values());
        return old;
    }

    public void delete(int id) {
        if (store.remove(id) == null) {
            throw new NoSuchElementException("Course not found");
        }
        fileStore.save(store.values());
    }

    public Course updateEnrolled(int id, int delta) {
        Course c = findById(id);
        int next = c.getEnrolled() + delta;

        if (next < 0 || next > c.getCapacity()) {
            throw new IllegalStateException("Invalid enrolled value");
        }

        c.setEnrolled(next);
        fileStore.save(store.values());
        return c;
    }

    private void validate(Course c) {
        if (c.getTitle() == null || c.getTitle().isBlank()) {
            throw new IllegalArgumentException("title required");
        }
        if (c.getCapacity() <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
    }
}
