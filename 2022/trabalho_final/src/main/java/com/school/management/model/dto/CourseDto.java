package com.school.management.model.dto;

import java.sql.Timestamp;
import java.time.Instant;

public class CourseDto {

    private Long id;

    private String name;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public CourseDto() {

    }

    public CourseDto(String name) {
        Timestamp ts = Timestamp.from(Instant.now());
        this.id = 0L;
        this.name = name;
        this.createdAt = ts;
        this.updatedAt = ts;
    }

    public CourseDto(Long id, String name, Timestamp createdAt, Timestamp updatedAt) {
        this(name);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
