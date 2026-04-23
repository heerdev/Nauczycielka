package com.nauczycielka.service;

import com.nauczycielka.model.StudentEnrollment;
import com.nauczycielka.repository.StudentEnrollmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class EnrollmentService {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final UserService userService;

    public EnrollmentService(StudentEnrollmentRepository enrollmentRepository,
                             CourseService courseService,
                             UserService userService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseService = courseService;
        this.userService = userService;
    }

    @Transactional
    public StudentEnrollment enrollStudentInCourse(Long courseId, Long studentId) {
        if (enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        var course = courseService.getCourseById(courseId);
        var student = userService.getUserById(studentId);

        StudentEnrollment enrollment = StudentEnrollment.builder()
                .course(course)
                .student(student)
                .isActive(true)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public List<StudentEnrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    public List<StudentEnrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public StudentEnrollment getEnrollmentById(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with ID: " + enrollmentId));
    }

    @Transactional
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        var enrollment = enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
    }
}
