package com.nauczycielka.repository;

import com.nauczycielka.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
    List<Transcript> findBySessionId(Long sessionId);
    List<Transcript> findBySessionIdAndIsProcessedFalse(Long sessionId);
}
