package org.example.aptus.service;

import org.example.aptus.dto.SubmitTestResponse;
import org.example.aptus.entity.QuestionPaperAudit;
import org.example.aptus.entity.QuestionPaperStatus;
import org.example.aptus.repository.QuestionPaperAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TestService {

    private final QuestionPaperAuditRepository auditRepository;
    private final EvaluationService evaluationService;

    public TestService(
            QuestionPaperAuditRepository auditRepository,
            EvaluationService evaluationService) {
        this.auditRepository = auditRepository;
        this.evaluationService = evaluationService;
    }

    @Transactional
    public SubmitTestResponse submitTest(
            Long auditId,
            Map<String, String> answers) {

        if (auditId == null) {
            throw new IllegalArgumentException(
                    "Assessment ID is required");
        }

        if (answers == null) {
            throw new IllegalArgumentException(
                    "Submitted answers are required");
        }

        QuestionPaperAudit audit = auditRepository.findById(auditId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Assessment not found for auditId: " + auditId));

        if (audit.getStatus() == QuestionPaperStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Assessment has already been submitted");
        }

        // Calculate the candidate's score.
        int obtainedMarks = evaluationService.evaluate(audit, answers);

        // Get the maximum marks stored when the assessment was created.
        Integer totalMarks = audit.getTotalMarks();

        if (totalMarks == null) {
            throw new IllegalStateException(
                    "Total marks are missing for auditId: " + auditId);
        }

        // Update the assessment record.
        audit.setObtainedMarks(obtainedMarks);
        audit.setStatus(QuestionPaperStatus.SUBMITTED);
        audit.setSubmittedAt(LocalDateTime.now());

        // Persist the changes.
        QuestionPaperAudit savedAudit =
                auditRepository.saveAndFlush(audit);

        // Return both obtained marks and total marks.
        return new SubmitTestResponse(
                savedAudit.getObtainedMarks(),
                savedAudit.getTotalMarks()
        );
    }
}

