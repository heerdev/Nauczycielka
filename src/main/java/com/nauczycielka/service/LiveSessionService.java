package com.nauczycielka.service;

import com.nauczycielka.model.LiveSession;
import com.nauczycielka.model.SessionStatus;
import com.nauczycielka.model.Transcript;
import com.nauczycielka.repository.LiveSessionRepository;
import com.nauczycielka.repository.TranscriptRepository;
import com.nauczycielka.service.ai.SummaryGenerator;
import com.nauczycielka.service.ai.VocabularyExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LiveSessionService {

    private final LiveSessionRepository liveSessionRepository;
    private final TranscriptRepository transcriptRepository;
    private final CourseService courseService;
    private final VocabularyService vocabularyService;
    private final CourseSummaryService summaryService;
    private final VocabularyExtractor vocabularyExtractor;
    private final SummaryGenerator summaryGenerator;

    public LiveSessionService(LiveSessionRepository liveSessionRepository,
                              TranscriptRepository transcriptRepository,
                              CourseService courseService,
                              VocabularyService vocabularyService,
                              CourseSummaryService summaryService,
                              VocabularyExtractor vocabularyExtractor,
                              SummaryGenerator summaryGenerator) {
        this.liveSessionRepository = liveSessionRepository;
        this.transcriptRepository = transcriptRepository;
        this.courseService = courseService;
        this.vocabularyService = vocabularyService;
        this.summaryService = summaryService;
        this.vocabularyExtractor = vocabularyExtractor;
        this.summaryGenerator = summaryGenerator;
    }

    @Transactional
    public LiveSession startSession(Long courseId, String title, String description) {
        var course = courseService.getCourseById(courseId);

        var existingSession = liveSessionRepository.findByCourseIdAndStatus(courseId, SessionStatus.IN_PROGRESS);
        if (existingSession.isPresent()) {
            throw new IllegalArgumentException("A session is already in progress for this course");
        }

        LiveSession session = LiveSession.builder()
                .title(title)
                .description(description)
                .course(course)
                .status(SessionStatus.IN_PROGRESS)
                .startedAt(LocalDateTime.now())
                .build();

        return liveSessionRepository.save(session);
    }

    @Transactional
    public void addTranscript(Long sessionId, String content, String speaker) {
        LiveSession session = getLiveSessionById(sessionId);

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Session is not in progress");
        }

        Transcript transcript = Transcript.builder()
                .content(content)
                .speaker(speaker)
                .session(session)
                .timestamp(LocalDateTime.now())
                .isProcessed(false)
                .build();

        transcriptRepository.save(transcript);
    }

    @Transactional
    @Async
    public void endSessionAndGenerateContent(Long sessionId) {
        LiveSession session = getLiveSessionById(sessionId);

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Session is not in progress");
        }

        // Mark session as completed
        session.setStatus(SessionStatus.COMPLETED);
        session.setEndedAt(LocalDateTime.now());
        liveSessionRepository.save(session);

        // Collect all transcripts for this session
        List<Transcript> transcripts = transcriptRepository.findBySessionId(sessionId);
        String fullTranscript = transcripts.stream()
                .map(Transcript::getContent)
                .collect(Collectors.joining(" "));

        if (fullTranscript.isBlank()) {
            log.warn("No transcripts found for session {}", sessionId);
            return;
        }

        long startTime = System.currentTimeMillis();

        // Generate vocabulary
        try {
            var vocabulary = vocabularyExtractor.extractVocabulary(fullTranscript);
            vocabularyService.addVocabularyToSession(session.getCourse().getId(), vocabulary);
            log.info("Generated {} vocabulary items for session {}", vocabulary.size(), sessionId);
        } catch (Exception e) {
            log.error("Error generating vocabulary for session {}", sessionId, e);
        }

        // Generate summary
        try {
            String summary = summaryGenerator.generateSummary(fullTranscript);
            summaryService.createCourseSummary(session.getCourse().getId(), sessionId, summary, "qwen2", System.currentTimeMillis() - startTime);
            log.info("Generated summary for session {}", sessionId);
        } catch (Exception e) {
            log.error("Error generating summary for session {}", sessionId, e);
        }

        // Mark transcripts as processed
        transcripts.forEach(t -> {
            t.setIsProcessed(true);
            transcriptRepository.save(t);
        });
    }

    public LiveSession getLiveSessionById(Long sessionId) {
        return liveSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Live session not found with ID: " + sessionId));
    }

    public List<LiveSession> getSessionsByCourse(Long courseId) {
        return liveSessionRepository.findByCourseId(courseId);
    }

    public List<LiveSession> getSessionsByStatus(SessionStatus status) {
        return liveSessionRepository.findByStatus(status);
    }
}
