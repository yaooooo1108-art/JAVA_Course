package com.group17.course_service.model;

public class Course {
    private int id;
    private String title;
    private int capacity;
    private int enrolled;

    public Course() {}

    public Course(int id, String title, int capacity, int enrolled) {
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.enrolled = enrolled;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getEnrolled() { return enrolled; }
    public void setEnrolled(int enrolled) { this.enrolled = enrolled; }
}
