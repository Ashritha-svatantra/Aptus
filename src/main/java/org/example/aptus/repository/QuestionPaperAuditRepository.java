package org.example.aptus.repository;


import org.example.aptus.entity.QuestionPaperAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionPaperAuditRepository
        extends JpaRepository<QuestionPaperAudit, Long> {
}