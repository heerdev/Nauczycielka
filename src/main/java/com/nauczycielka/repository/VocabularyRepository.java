package com.nauczycielka.repository;

import com.nauczycielka.model.VocabularyItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<VocabularyItem, Long> {
    List<VocabularyItem> findByCourseId(Long courseId);
    List<VocabularyItem> findByCourseIdAndStudentId(Long courseId, Long studentId);
    Optional<VocabularyItem> findByCourseIdAndWord(Long courseId, String word);
}
