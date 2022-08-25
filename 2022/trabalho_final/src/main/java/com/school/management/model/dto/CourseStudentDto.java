package com.school.management.model.dto;

import java.sql.Timestamp;
import java.time.Instant;

public class CourseStudentDto {

    private Long id;

    private Long studentId;

    private Long courseId;

    private Timestamp createdAt;

    public CourseStudentDto() {

    }

    public CourseStudentDto(Long studentId, Long courseId) {
        Timestamp ts = Timestamp.from(Instant.now());
        this.id = 0L;
        this.studentId = studentId;
        this.courseId = courseId;
        this.createdAt = ts;
    }

    public CourseStudentDto(Long id, Long studentId, Long courseId, Timestamp ts) {
        this(studentId, courseId);
        this.id = id;
        this.createdAt = ts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
