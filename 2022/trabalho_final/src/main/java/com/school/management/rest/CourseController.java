package com.school.management.rest;

import com.school.management.model.dto.CourseDto;
import com.school.management.model.dto.CourseStudentDto;
import com.school.management.model.dto.StudentDto;
import com.school.management.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/courses")
public class CourseController {

	private final CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping(value = "/")
	@ResponseStatus(HttpStatus.OK)
	public List<CourseDto> getCourses(@RequestParam(name = "without-students") Optional<Boolean> withoutStudents) {
		return this.courseService.getCourses(withoutStudents.orElse(false));
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CourseDto getCourse(@PathVariable Long id) {
		return courseService.getCourse(id);
	}

	@GetMapping(value = "/{id}/students")
	@ResponseStatus(HttpStatus.OK)
	public List<StudentDto> getStudentsFromCourse(@PathVariable Long id) {
		return courseService.enrolled(id);
	}

	@GetMapping(value = "/students")
	@ResponseStatus(HttpStatus.OK)
	public List<CourseStudentDto> getRelations() {
		return courseService.relations();
	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public CourseDto updateCourse(@PathVariable Long id, @RequestBody CourseDto courseDto) {
		courseDto.setId(id);
		return courseService.updateCourse(courseDto);
	}

	@PutMapping(value = "/{id}/students")
	@ResponseStatus(HttpStatus.OK)
	public List<Long> updateCourseStudents(@PathVariable Long id, @RequestBody List<Long> studentIds) {
		return courseService.enroll(id, studentIds);
	}

	@PostMapping(value = "/")
	@ResponseStatus(HttpStatus.CREATED)
	public List<CourseDto> createCourses(@RequestBody List<CourseDto> courseDtoList) {
		return courseService.createCourses(courseDtoList);
	}

	@DeleteMapping(value = "/")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCourses(@RequestParam(name = "confirm-deletion") Optional<Boolean> confirmDeletion) {
		courseService.deleteAllCourses(confirmDeletion.orElse(false));
	}

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCourse(@PathVariable Long id, @RequestParam(name = "confirm-deletion") Optional<Boolean> confirmDeletion) {
		courseService.deleteCourse(id, confirmDeletion.orElse(false));
	}

}
