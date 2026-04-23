package com.nauczycielka.service;

import com.nauczycielka.model.VocabularyItem;
import com.nauczycielka.repository.VocabularyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final CourseService courseService;

    public VocabularyService(VocabularyRepository vocabularyRepository, CourseService courseService) {
        this.vocabularyRepository = vocabularyRepository;
        this.courseService = courseService;
    }

    @Transactional
    public void addVocabularyToSession(Long courseId, Set<String> words) {
        var course = courseService.getCourseById(courseId);

        for (String word : words) {
            // Check if vocabulary already exists for this course
            var existing = vocabularyRepository.findByCourseIdAndWord(courseId, word);

            if (existing.isEmpty()) {
                VocabularyItem vocabularyItem = VocabularyItem.builder()
                        .word(word)
                        .course(course)
                        .isSharedCourseLevel(true)
                        .build();

                vocabularyRepository.save(vocabularyItem);
            }
        }
    }

    @Transactional
    public VocabularyItem addVocabularyForStudent(Long courseId, Long studentId, String word) {
        var course = courseService.getCourseById(courseId);

        var existing = vocabularyRepository.findByCourseIdAndWord(courseId, word);

        if (existing.isPresent()) {
            return existing.get();
        }

        VocabularyItem vocabularyItem = VocabularyItem.builder()
                .word(word)
                .course(course)
                .student(null) // Can be set if student-specific
                .isSharedCourseLevel(true)
                .build();

        return vocabularyRepository.save(vocabularyItem);
    }

    public List<VocabularyItem> getVocabularyForCourse(Long courseId) {
        return vocabularyRepository.findByCourseId(courseId);
    }

    public List<VocabularyItem> getVocabularyForStudentInCourse(Long courseId, Long studentId) {
        // Return course-level vocabulary for the student
        return vocabularyRepository.findByCourseId(courseId);
    }

    public VocabularyItem getVocabularyItemById(Long itemId) {
        return vocabularyRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary item not found with ID: " + itemId));
    }
}
