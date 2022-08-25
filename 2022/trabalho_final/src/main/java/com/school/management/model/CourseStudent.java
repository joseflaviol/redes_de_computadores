package com.school.management.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class CourseStudent {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long studentId;

        private Long courseId;

        private Timestamp createdAt;

        public CourseStudent() {

        }
        public CourseStudent(Long studentId, Long courseId, Timestamp createdAt) {
            this.studentId = studentId;
            this.courseId = courseId;
            this.createdAt = createdAt;
        }

        public CourseStudent(Long id, Long studentId, Long courseId, Timestamp createdAt) {
            this(studentId, courseId, createdAt);
            this.id = id;
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
