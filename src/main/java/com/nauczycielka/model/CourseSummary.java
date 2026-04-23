package com.nauczycielka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_summaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private LiveSession session;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(name = "ai_model")
    private String aiModel;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
}
