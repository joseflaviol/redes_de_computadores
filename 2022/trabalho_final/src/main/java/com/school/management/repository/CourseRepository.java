package com.school.management.repository;

import com.school.management.model.Course;
import com.school.management.model.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
