package com.nauczycielka.controller;

import com.nauczycielka.dto.EnrollmentRequest;
import com.nauczycielka.model.StudentEnrollment;
import com.nauczycielka.service.EnrollmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/enrollments")
@Slf4j
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<StudentEnrollment> enrollStudent(@PathVariable Long courseId,
                                                           @RequestBody EnrollmentRequest request) {
        log.info("Enrolling student: {} in course: {}", request.studentId(), courseId);
        StudentEnrollment enrollment = enrollmentService.enrollStudentInCourse(courseId, request.studentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @GetMapping
    public ResponseEntity<List<StudentEnrollment>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        log.info("Fetching enrollments for course: {}", courseId);
        List<StudentEnrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    @DeleteMapping("/student/{studentId}")
    public ResponseEntity<Void> removeStudentFromCourse(@PathVariable Long courseId,
                                                        @PathVariable Long studentId) {
        log.info("Removing student: {} from course: {}", studentId, courseId);
        enrollmentService.removeStudentFromCourse(courseId, studentId);
        return ResponseEntity.noContent().build();
    }
}
