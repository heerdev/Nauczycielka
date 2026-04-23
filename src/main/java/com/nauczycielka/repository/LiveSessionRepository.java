package com.nauczycielka.repository;

import com.nauczycielka.model.LiveSession;
import com.nauczycielka.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveSessionRepository extends JpaRepository<LiveSession, Long> {
    List<LiveSession> findByCourseId(Long courseId);
    List<LiveSession> findByStatus(SessionStatus status);
    Optional<LiveSession> findByCourseIdAndStatus(Long courseId, SessionStatus status);
}
