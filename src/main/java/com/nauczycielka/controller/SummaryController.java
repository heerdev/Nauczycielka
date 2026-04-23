package com.nauczycielka.controller;

import com.nauczycielka.model.CourseSummary;
import com.nauczycielka.service.CourseSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/summary")
@Slf4j
public class SummaryController {

    private final CourseSummaryService summaryService;

    public SummaryController(CourseSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping
    public ResponseEntity<CourseSummary> getSummaryForCourse(@PathVariable Long courseId) {
        log.info("Fetching summary for course: {}", courseId);
        return summaryService.getSummaryByCourse(courseId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<CourseSummary> getSummaryForSession(@PathVariable Long courseId,
                                                              @PathVariable Long sessionId) {
        log.info("Fetching summary for session: {}", sessionId);
        return summaryService.getSummaryBySession(sessionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
