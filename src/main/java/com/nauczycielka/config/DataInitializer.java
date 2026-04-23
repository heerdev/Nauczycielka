package com.nauczycielka.config;

import com.nauczycielka.model.Course;
import com.nauczycielka.model.User;
import com.nauczycielka.model.UserRole;
import com.nauczycielka.repository.CourseRepository;
import com.nauczycielka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository, CourseRepository courseRepository) {
        return args -> {
            // Create sample instructor
            if (userRepository.findByEmail("instructor@example.com").isEmpty()) {
                User instructor = User.builder()
                        .email("instructor@example.com")
                        .fullName("Dr. John Smith")
                        .password("hashed_password_123")
                        .role(UserRole.INSTRUCTOR)
                        .build();
                userRepository.save(instructor);
                log.info("Created sample instructor: {}", instructor.getEmail());

                // Create sample students
                for (int i = 1; i <= 3; i++) {
                    User student = User.builder()
                            .email("student" + i + "@example.com")
                            .fullName("Student " + i)
                            .password("hashed_password_" + i)
                            .role(UserRole.STUDENT)
                            .build();
                    userRepository.save(student);
                    log.info("Created sample student: {}", student.getEmail());
                }

                // Create sample course
                User savedInstructor = userRepository.findByEmail("instructor@example.com").get();
                Course course = Course.builder()
                        .title("Python Fundamentals")
                        .description("Learn Python basics including variables, functions, and object-oriented programming")
                        .instructor(savedInstructor)
                        .build();
                courseRepository.save(course);
                log.info("Created sample course: {}", course.getTitle());
            }
        };
    }
}
