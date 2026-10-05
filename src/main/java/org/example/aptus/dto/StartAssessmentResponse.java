package org.example.aptus.dto;

import java.util.List;

public class StartAssessmentResponse {

    private Long auditId;
    private List<AssessmentQuestionResponse> questions;

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public List<AssessmentQuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AssessmentQuestionResponse> questions) {
        this.questions = questions;
    }
}