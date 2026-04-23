package com.nauczycielka.service;

import com.nauczycielka.model.Course;
import com.nauczycielka.model.User;
import com.nauczycielka.repository.CourseRepository;
import com.nauczycielka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Course createCourse(String title, String description, Long instructorId) {
        if (courseRepository.existsByTitle(title)) {
            throw new IllegalArgumentException("Course with title '" + title + "' already exists");
        }

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        Course course = Course.builder()
                .title(title)
                .description(description)
                .instructor(instructor)
                .build();

        return courseRepository.save(course);
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
    }

    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course updateCourse(Long courseId, String title, String description) {
        Course course = getCourseById(courseId);

        if (title != null && !title.isBlank()) {
            course.setTitle(title);
        }

        if (description != null) {
            course.setDescription(description);
        }

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }
}
