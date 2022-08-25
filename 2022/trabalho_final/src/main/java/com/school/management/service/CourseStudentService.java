package com.school.management.service;

import com.school.management.model.dto.CourseDto;
import com.school.management.model.dto.CourseStudentDto;
import com.school.management.repository.CourseStudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseStudentService {

    private final CourseStudentRepository courseStudentRepository;

    public CourseStudentService(CourseStudentRepository courseStudentRepository) {
        this.courseStudentRepository = courseStudentRepository;
    }

    public CourseDto getCoursesByStudent(Long idStudent) {
        //this.courseStudentRepository.findAll()
        return null;
    }

    public List<CourseStudentDto> createRelations(Long idStudent, List<Long> ids) {
        /*List<CourseStudentDto> relations = this.courseStudentRepository.saveAll(ids.stream()
                .map(id -> new CourseStudentDto(idStudent, id))
                .collect(Collectors.toList())
        );*/
        return null;
    }

}
