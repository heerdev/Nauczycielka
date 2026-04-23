package com.nauczycielka.controller;

import com.nauczycielka.dto.CreateCourseRequest;
import com.nauczycielka.dto.UpdateCourseRequest;
import com.nauczycielka.model.Course;
import com.nauczycielka.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody CreateCourseRequest request) {
        log.info("Creating course with title: {}", request.title());
        Course course = courseService.createCourse(request.title(), request.description(), request.instructorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        log.info("Fetching all courses");
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId) {
        log.info("Fetching course with ID: {}", courseId);
        Course course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable Long instructorId) {
        log.info("Fetching courses for instructor: {}", instructorId);
        List<Course> courses = courseService.getCoursesByInstructor(instructorId);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long courseId, @RequestBody UpdateCourseRequest request) {
        log.info("Updating course with ID: {}", courseId);
        Course course = courseService.updateCourse(courseId, request.title(), request.description());
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        log.info("Deleting course with ID: {}", courseId);
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}
