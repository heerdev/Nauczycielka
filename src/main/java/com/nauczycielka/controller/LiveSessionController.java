package com.nauczycielka.controller;

import com.nauczycielka.dto.CreateLiveSessionRequest;
import com.nauczycielka.dto.TranscriptRequest;
import com.nauczycielka.model.LiveSession;
import com.nauczycielka.service.LiveSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/sessions")
@Slf4j
public class LiveSessionController {

    private final LiveSessionService liveSessionService;

    public LiveSessionController(LiveSessionService liveSessionService) {
        this.liveSessionService = liveSessionService;
    }

    @PostMapping
    public ResponseEntity<LiveSession> startSession(@PathVariable Long courseId,
                                                    @RequestBody CreateLiveSessionRequest request) {
        log.info("Starting live session for course: {}", courseId);
        LiveSession session = liveSessionService.startSession(courseId, request.title(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<LiveSession> getSession(@PathVariable Long courseId,
                                                  @PathVariable Long sessionId) {
        log.info("Fetching session: {} for course: {}", sessionId, courseId);
        LiveSession session = liveSessionService.getLiveSessionById(sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping
    public ResponseEntity<List<LiveSession>> getSessionsByCourse(@PathVariable Long courseId) {
        log.info("Fetching all sessions for course: {}", courseId);
        List<LiveSession> sessions = liveSessionService.getSessionsByCourse(courseId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/{sessionId}/transcripts")
    public ResponseEntity<Void> addTranscript(@PathVariable Long courseId,
                                              @PathVariable Long sessionId,
                                              @RequestBody TranscriptRequest request) {
        log.info("Adding transcript to session: {}", sessionId);
        liveSessionService.addTranscript(sessionId, request.content(), request.speaker());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<Void> endSession(@PathVariable Long courseId,
                                           @PathVariable Long sessionId) {
        log.info("Ending session: {} and generating content", sessionId);
        liveSessionService.endSessionAndGenerateContent(sessionId);
        return ResponseEntity.ok().build();
    }
}
