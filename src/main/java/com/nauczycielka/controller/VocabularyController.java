package com.nauczycielka.controller;

import com.nauczycielka.model.VocabularyItem;
import com.nauczycielka.service.VocabularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/vocabulary")
@Slf4j
public class VocabularyController {

    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    @GetMapping
    public ResponseEntity<List<VocabularyItem>> getVocabularyForCourse(@PathVariable Long courseId) {
        log.info("Fetching vocabulary for course: {}", courseId);
        List<VocabularyItem> vocabulary = vocabularyService.getVocabularyForCourse(courseId);
        return ResponseEntity.ok(vocabulary);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<VocabularyItem>> getVocabularyForStudent(@PathVariable Long courseId,
                                                                         @PathVariable Long studentId) {
        log.info("Fetching vocabulary for student: {} in course: {}", studentId, courseId);
        List<VocabularyItem> vocabulary = vocabularyService.getVocabularyForStudentInCourse(courseId, studentId);
        return ResponseEntity.ok(vocabulary);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<VocabularyItem> getVocabularyItem(@PathVariable Long courseId,
                                                            @PathVariable Long itemId) {
        log.info("Fetching vocabulary item: {}", itemId);
        VocabularyItem item = vocabularyService.getVocabularyItemById(itemId);
        return ResponseEntity.ok(item);
    }
}
