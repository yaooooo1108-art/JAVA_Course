package com.group17.enrollment_service.model;

public class EnrollmentRecord {
    private String studentId;
    private int courseId;

    public EnrollmentRecord() {}

    public EnrollmentRecord(String studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
}
