package com.school.management.service;

import com.school.management.model.Course;
import com.school.management.model.CourseStudent;
import com.school.management.model.dto.CourseDto;
import com.school.management.model.dto.CourseStudentDto;
import com.school.management.model.dto.StudentDto;
import com.school.management.repository.CourseRepository;
import com.school.management.repository.CourseStudentRepository;
import com.school.management.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final CourseStudentRepository courseStudentRepository;

    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository, CourseStudentRepository courseStudentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.courseStudentRepository = courseStudentRepository;
    }

    public List<CourseDto> getCourses(boolean withoutStudents) {
        return courseRepository.findAll().stream()
                .filter(course -> {
                    if (withoutStudents) {
                        return courseStudentRepository.findAllByCourseId(course.getId()).size() == 0;
                    }
                    return true;
                })
                .map(course -> new CourseDto(course.getId(), course.getName(), course.getCreatedAt(), course.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    public CourseDto getCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> new CourseDto(course.getId(), course.getName(), course.getCreatedAt(), course.getUpdatedAt()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found."));
    }

    @Transactional
    public CourseDto updateCourse(CourseDto courseDto) {
        Course course = courseRepository.findById(courseDto.getId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Course not found."));

        Boolean updated = false;
        if (courseDto.getName() != null && !courseDto.getName().isBlank() && !courseDto.getName().equals(course.getName())) {
            course.setName(courseDto.getName());
            updated = true;
        }

        if (updated) {
            course.setUpdatedAt(Timestamp.from(Instant.now()));
            course = courseRepository.save(course);
        }

        return new CourseDto(course.getId(), course.getName(), course.getCreatedAt(), course.getUpdatedAt());
    }

    public List<CourseDto> createCourses(List<CourseDto> coursesDto) {
        if (coursesDto.size() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "A request can not contain more than 50 courses.");
        }

        Timestamp ts = Timestamp.from(Instant.now());
        List<Course> l = courseRepository.saveAll(coursesDto.stream()
                .filter(s -> s.getName() != null && !s.getName().isBlank() )
                .map(courseDto -> new Course(courseDto.getName(),
                        ts,
                        ts))
                .collect(Collectors.toList()));

        return l.stream()
                .map(course -> new CourseDto(course.getId(),
                        course.getName(),
                        course.getCreatedAt(),
                        course.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAllCourses(Boolean confirmDeletion) {
        if (confirmDeletion) {
            courseStudentRepository.deleteAll();
            courseRepository.deleteAll();
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "To delete ALL courses and courses-courses relationships, inform confirm-deletion=true as a query param.");
        }
    }

    @Transactional
    public void deleteCourse(Long id, Boolean confirmDeletion) {
        if (confirmDeletion) {
            courseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Course not found."));
            courseStudentRepository.deleteAllByCourseId(id);
            courseRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "To delete the course and course-courses relationships, inform confirm-deletion=true as a query param.");
        }
    }

    public List<StudentDto> enrolled(Long id) {
        courseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Course not found"
        ));

        return courseStudentRepository.findAllByCourseId(id).stream().map(courseStudent -> {
            return studentRepository.findById(courseStudent.getStudentId()).map(student -> new StudentDto(
                    student.getId(),
                    student.getName(),
                    student.getAddress(),
                    student.getCreatedAt(),
                    student.getUpdatedAt()
            )).get();
        }).collect(Collectors.toList());
    }

    public List<CourseStudentDto> relations() {
        return courseStudentRepository.findAllByOrderByCourseIdAsc()
                .stream()
                .map(courseStudent -> new CourseStudentDto(
                        courseStudent.getId(),
                        courseStudent.getStudentId(),
                        courseStudent.getCourseId(),
                        courseStudent.getCreatedAt()
                    )
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Long> enroll(Long id, List<Long> studentIds) {
        courseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Course not found"
        ));

        if (studentIds.size() > 50) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "A course cannot have more than 50 students"
            );
        }

        courseStudentRepository.deleteAllByCourseId(id);

        List<Long> res = new ArrayList<>();
        res.add(id);

        res.addAll( studentIds.stream()
                .filter(studentId -> {
                    studentRepository.findById(studentId).orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Student not found"
                    ));

                    if (courseStudentRepository.findAllByStudentId(studentId).size() + 1 > 5) {
                        throw new ResponseStatusException(
                                HttpStatus.FORBIDDEN, "A student cannot be enrolled in more than 5 courses"
                        );
                    }

                    return true;
                })
                .map(studentId -> {
                    courseStudentRepository.save(new CourseStudent(studentId, id, Timestamp.from(Instant.now())));
                    return studentId;
                })
                .collect(Collectors.toList())
        );
        return res;
    }
}
