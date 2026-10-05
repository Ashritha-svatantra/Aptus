package org.example.aptus.service;

import org.example.aptus.entity.QuestionPaperAudit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EvaluationService {

    public int evaluate(
            QuestionPaperAudit audit,
            Map<String, String> answers) {
        if (audit == null) {
            throw new IllegalArgumentException(
                    "Assessment audit cannot be null"
            );
        }

        if (audit.getQuestions() == null
                || audit.getQuestions().isEmpty()) {
            throw new IllegalStateException(
                    "Questions are missing for auditId: "
                            + audit.getAuditId()
            );
        }

        if (answers == null) {
            throw new IllegalArgumentException(
                    "Submitted answers are required"
            );
        }

        int obtainedMarks = 0;

        List<Map<String, Object>> questions =
                audit.getQuestions();

        for (Map<String, Object> question : questions) {

            if (question == null) {
                continue;
            }

            Object idObject = question.get("questionId");
            Object correctObject = question.get("correctAnswer");
            Object marksObject = question.get("marks");

            if (idObject == null
                    || correctObject == null
                    || !(marksObject instanceof Number)) {

                throw new IllegalStateException(
                        "Invalid evaluation data for auditId: "
                                + audit.getAuditId()
                );
            }

            String questionId =
                    String.valueOf(idObject).trim();

            String correctAnswer =
                    String.valueOf(correctObject).trim();

            int marks =
                    ((Number) marksObject).intValue();

            if (questionId.isEmpty()
                    || correctAnswer.isEmpty()
                    || marks < 0) {

                throw new IllegalStateException(
                        "Invalid question ID, correct answer, or marks "
                                + "in auditId: "
                                + audit.getAuditId()
                );
            }

            String candidateAnswer =
                    answers.get(questionId);

            if (candidateAnswer == null
                    || candidateAnswer.isBlank()) {

                continue;
            }

            candidateAnswer =
                    candidateAnswer.trim().toUpperCase();

            correctAnswer =
                    correctAnswer.toUpperCase();

            if (candidateAnswer.equals(correctAnswer)) {

                obtainedMarks =
                        Math.addExact(obtainedMarks, marks);

            }
        }
        return obtainedMarks;
    }
}