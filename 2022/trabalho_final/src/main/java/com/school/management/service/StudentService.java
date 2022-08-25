package com.school.management.service;


import com.school.management.model.Course;
import com.school.management.model.CourseStudent;
import com.school.management.model.Student;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;
	private final CourseStudentRepository courseStudentRepository;

	public StudentService(StudentRepository studentRepository, CourseRepository courseRepository, CourseStudentRepository courseStudentRepository) {
		this.studentRepository = studentRepository;
		this.courseRepository = courseRepository;
		this.courseStudentRepository = courseStudentRepository;
	}

	public List<StudentDto> getStudents(boolean withoutCourses) {
		return studentRepository.findAll().stream()
				.filter(student -> {
					if (withoutCourses) {
						return courseStudentRepository.findAllByStudentId(student.getId()).size() == 0;
					}
					return true;
				})
				.map(student -> new StudentDto(student.getId(), student.getName(), student.getAddress(), student.getCreatedAt(), student.getUpdatedAt()))
				.collect(Collectors.toList());
	}

	public StudentDto getStudent(Long id) {
		return studentRepository.findById(id)
			.map(student -> new StudentDto(student.getId(), student.getName(), student.getAddress(), student.getCreatedAt(), student.getUpdatedAt()))
			.orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND, "Student not found."));
	}

	@Transactional
	public StudentDto updateStudent(StudentDto studentDto) {
		Student student = studentRepository.findById(studentDto.getId()).orElseThrow(() -> new ResponseStatusException(
			HttpStatus.NOT_FOUND, "Student not found."));

		Boolean updated = false;
		if (studentDto.getName() != null && !studentDto.getName().isBlank() && !studentDto.getName().equals(student.getName())) {
			student.setName(studentDto.getName());
			updated = true;
		}
		if (studentDto.getAddress() != null && !studentDto.getAddress().isBlank() && !studentDto.getAddress().equals(student.getAddress())) {
			student.setAddress(studentDto.getAddress());
			updated = true;
		}

		if (updated) {
			student.setUpdatedAt(Timestamp.from(Instant.now()));
			student = studentRepository.save(student);
		}

		return new StudentDto(student.getId(), student.getName(), student.getAddress(), student.getCreatedAt(), student.getUpdatedAt());
	}

	public List<StudentDto> createStudents(List<StudentDto> studentsDto) {
		if (studentsDto.size() > 50) {
			throw new ResponseStatusException(
				HttpStatus.FORBIDDEN, "A request can not contain more than 50 students.");
		}

		Timestamp ts = Timestamp.from(Instant.now());
		List<Student> l = studentRepository.saveAll(studentsDto.stream()
			.filter(s -> s.getName() != null && !s.getName().isBlank() && s.getAddress() != null && !s.getAddress().isBlank())
			.map(studentDto -> new Student(studentDto.getName(),
				studentDto.getAddress(),
				ts,
				ts))
			.collect(Collectors.toList()));

		return l.stream()
			.map(student -> new StudentDto(student.getId(),
				student.getName(),
				student.getAddress(),
				student.getCreatedAt(),
				student.getUpdatedAt()))
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteAllStudents(Boolean confirmDeletion) {
		if (confirmDeletion) {
			courseStudentRepository.deleteAll();
			studentRepository.deleteAll();
		} else {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"To delete ALL students and students-courses relationships, inform confirm-deletion=true as a query param.");
		}
	}

	@Transactional
	public void deleteStudent(Long id, Boolean confirmDeletion) {
		if (confirmDeletion) {
			studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND, "Student not found."));
			courseStudentRepository.deleteByStudentId(id);
			studentRepository.deleteById(id);
		} else {
			throw new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"To delete the student and student-courses relationships, inform confirm-deletion=true as a query param.");
		}
	}

    public List<CourseDto> enrolled(Long id) {
		studentRepository.findById(id).orElseThrow( () -> new ResponseStatusException(
			HttpStatus.NOT_FOUND, "Student not found."
		));

		return courseStudentRepository.findAllByStudentId(id)
				.stream()
				.map(courseStudent -> {
					return courseRepository.findById(courseStudent.getCourseId())
						.map(course -> new CourseDto(course.getId(), course.getName(), course.getCreatedAt(), course.getUpdatedAt()))
						.get();
				}).collect(Collectors.toList());
    }

	public List<CourseStudentDto> relations() {
		return courseStudentRepository.findAllByOrderByStudentIdAsc()
				.stream()
				.map(courseStudent -> new CourseStudentDto(
					courseStudent.getId(),
					courseStudent.getStudentId(),
					courseStudent.getCourseId(),
					courseStudent.getCreatedAt()
				))
				.collect(Collectors.toList());
	}

	@Transactional
	public List<Long> enroll(Long id, List<Long> courseIds) {
		studentRepository.findById(id).orElseThrow( () -> new ResponseStatusException(
				HttpStatus.NOT_FOUND, "Student not found."
		));

		if (courseStudentRepository.findAllByStudentId(id).size() + courseIds.size() > 5) {
			throw new ResponseStatusException(
				HttpStatus.FORBIDDEN, "Student cannot be enrolled in  more than 5 courses"
			);
		}

		List<Long> res = new ArrayList<>();
		res.add(id);

		res.addAll( courseIds.stream().map(courseId -> {

			courseRepository.findById(courseId).orElseThrow(
					() -> new ResponseStatusException(
							HttpStatus.NOT_FOUND, "Course not found."
					)
			);

			if (courseStudentRepository.findAllByCourseId(courseId).size() + 1 > 50) {
				throw new ResponseStatusException(
						HttpStatus.FORBIDDEN, "Course cannot have more than 50 students"
				);
			}

			courseStudentRepository.save(new CourseStudent(id, courseId, Timestamp.from(Instant.now())));

			return courseId;
		}).collect(Collectors.toList()));

		return res;
	}
}
