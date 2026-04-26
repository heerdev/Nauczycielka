package com.nauczycielka.service;

import com.nauczycielka.model.CourseSummary;
import com.nauczycielka.repository.CourseSummaryRepository;
import com.nauczycielka.repository.LiveSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class CourseSummaryService {

    private final CourseSummaryRepository summaryRepository;
    private final CourseService courseService;
    private final LiveSessionRepository liveSessionRepository;

    public CourseSummaryService(CourseSummaryRepository summaryRepository,
                                CourseService courseService,
                                LiveSessionRepository liveSessionRepository) {
        this.summaryRepository = summaryRepository;
        this.courseService = courseService;
        this.liveSessionRepository = liveSessionRepository;
    }

    @Transactional
    public CourseSummary createCourseSummary(Long courseId, Long sessionId, String summary, String aiModel, Long processingTimeMs) {
        var course = courseService.getCourseById(courseId);
        var session = liveSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Live session not found with ID: " + sessionId));

        CourseSummary courseSummary = CourseSummary.builder()
                .course(course)
                .session(session)
                .summary(summary)
                .aiModel(aiModel)
                .processingTimeMs(processingTimeMs)
                .build();

        return summaryRepository.save(courseSummary);
    }

    public Optional<CourseSummary> getSummaryByCourse(Long courseId) {
        return summaryRepository.findByCourseId(courseId);
    }

    public Optional<CourseSummary> getSummaryBySession(Long sessionId) {
        return summaryRepository.findBySessionId(sessionId);
    }

    public CourseSummary getSummaryById(Long summaryId) {
        return summaryRepository.findById(summaryId)
                .orElseThrow(() -> new IllegalArgumentException("Course summary not found with ID: " + summaryId));
    }
}
