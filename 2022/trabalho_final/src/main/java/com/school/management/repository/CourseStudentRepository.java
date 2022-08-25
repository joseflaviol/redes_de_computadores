package com.school.management.repository;

import com.school.management.model.CourseStudent;
import com.school.management.model.dto.CourseStudentDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {

    List<CourseStudent> findAllByStudentId(Long id);

    List<CourseStudent> findAllByOrderByStudentIdAsc();

    List<CourseStudent> findAllByCourseId(Long id);

    void deleteByStudentId(Long id);
}
