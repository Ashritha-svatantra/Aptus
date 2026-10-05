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

        int obtainedMarks = 0;

        List<Map<String, Object>> questions =
                audit.getQuestions();

        for (Map<String, Object> question : questions) {

            Object idObject = question.get("questionId");
            Object correctObject = question.get("correctAnswer");
            Object marksObject = question.get("marks");

            String questionId =
                    String.valueOf(idObject).trim();

            String correctAnswer =
                    String.valueOf(correctObject).trim();

            int marks =
                    ((Number) marksObject).intValue();

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