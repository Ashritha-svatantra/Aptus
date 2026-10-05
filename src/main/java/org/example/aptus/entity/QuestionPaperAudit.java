package org.example.aptus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "question_paper_audit", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class QuestionPaperAudit {

    @Id
    @SequenceGenerator(
            name = "question_paper_audit_generator",
            sequenceName = "question_paper_audit_audit_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "question_paper_audit_generator"
    )
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "questions",
            columnDefinition = "jsonb",
            nullable = false
    )
    private List<Map<String, Object>> questions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "obtained_marks", nullable = false)
    private Integer obtainedMarks = 0;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "question_paper_status"
    )
    private QuestionPaperStatus status =
            QuestionPaperStatus.CREATED;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 0;

    @Column(name = "fullscreen_exit_count", nullable = false)
    private Integer fullscreenExitCount = 0;

    @Column(name = "total_away_seconds", nullable = false)
    private Long totalAwaySeconds = 0L;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks = 0;
}