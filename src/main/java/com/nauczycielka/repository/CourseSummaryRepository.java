package com.nauczycielka.repository;

import com.nauczycielka.model.CourseSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseSummaryRepository extends JpaRepository<CourseSummary, Long> {
    Optional<CourseSummary> findByCourseId(Long courseId);
    Optional<CourseSummary> findBySessionId(Long sessionId);
}
