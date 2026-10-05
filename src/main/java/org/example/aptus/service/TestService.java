package org.example.aptus.service;

import lombok.RequiredArgsConstructor;
import org.example.aptus.dto.SubmitTestResponse;
import org.example.aptus.entity.QuestionPaperAudit;
import org.example.aptus.entity.QuestionPaperStatus;
import org.example.aptus.repository.QuestionPaperAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestService {

    private final QuestionPaperAuditRepository auditRepository;
    private final EvaluationService evaluationService;

    @Transactional
    public SubmitTestResponse submitTest(
            Long auditId,
            Map<String, String> answers) {

        QuestionPaperAudit audit =
                auditRepository.findById(auditId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Assessment not found for auditId: "
                                                + auditId
                                ));

        if (audit.getStatus() == QuestionPaperStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Assessment has already been submitted"
            );
        }

        int obtainedMarks =
                evaluationService.evaluate(audit, answers);

        audit.setObtainedMarks(obtainedMarks);
        audit.setStatus(QuestionPaperStatus.SUBMITTED);
        audit.setSubmittedAt(LocalDateTime.now());

        QuestionPaperAudit savedAudit =
                auditRepository.save(audit);

        return new SubmitTestResponse(
                savedAudit.getObtainedMarks(),
                savedAudit.getTotalMarks()
        );
    }
}