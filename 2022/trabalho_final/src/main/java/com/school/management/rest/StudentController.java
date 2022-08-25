package com.school.management.rest;

import com.school.management.model.dto.CourseDto;
import com.school.management.model.dto.CourseStudentDto;
import com.school.management.model.dto.StudentDto;
import com.school.management.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET methods (retrieving info)
     */

    /**
     * HTTP method: GET
     * <p>
     * //@param withoutCourses = true --> return the list of students without any course (default: false).
     *
     * @return the list of students.
     */
    @GetMapping(value = "/")
    @ResponseStatus(HttpStatus.OK)
	public List<StudentDto> getStudents(@RequestParam(name = "without-courses") Optional<Boolean> withoutCourses) {
        return studentService.getStudents(withoutCourses.orElse(false));
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentDto getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StudentDto updateStudent(@PathVariable Long id, @RequestBody StudentDto studentDto) {
        studentDto.setId(id);
        return studentService.updateStudent(studentDto);
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public List<StudentDto> createStudents(@RequestBody List<StudentDto> studentDtoList) {
        return studentService.createStudents(studentDtoList);
    }

    @DeleteMapping(value = "/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudents(@RequestParam(name = "confirm-deletion") Optional<Boolean> confirmDeletion) {
        studentService.deleteAllStudents(confirmDeletion.orElse(false));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id, @RequestParam(name = "confirm-deletion") Optional<Boolean> confirmDeletion) {
        studentService.deleteStudent(id, confirmDeletion.orElse(false));
    }

    /**
     *
     * TODO methods
     *
     */

    /**
     * HTTP method: GET
     * <p>
     * TODO
     *
     * @param id = the student id.
     * @return list of courses the student is enrolled.
     */
    @GetMapping(value = "/{id}/courses")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseDto> getCoursesFromStudent(@PathVariable Long id) {
        return studentService.enrolled(id);
    }

    /**
     * HTTP method: GET
     * <p>
     * TODO
     *
     * @return list of relationships between students and courses, ordered by student and course.
     */
    @GetMapping(value = "/courses")
    @ResponseStatus(HttpStatus.OK)
    public List<CourseStudentDto> getRelations() {
        return studentService.relations();
    }

    /**
     * HTTP method: PUT
     * <p>
     * TODO
     *
     * @param id        = the student id.
     * @param courseIds = the ids of the courses to enroll the student. Limited to 5 courses.
     *                  Ex: [1, 2, 3]
     * @return a list containing the student id and the enrolled courses.
     */
    @PutMapping(value = "/{id}/courses")
    @ResponseStatus(HttpStatus.OK)
    public List<Long> updateStudentCourses(@PathVariable Long id, @RequestBody List<Long> courseIds) {
        return studentService.enroll(id, courseIds);
    }
}
