
package org.example.aptus.service;


import org.example.aptus.entity.QuestionPaperAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EvaluationService {

    private static final Logger log =
            LoggerFactory.getLogger(EvaluationService.class);

    /**
     * Evaluates the candidate's answers against the saved assessment
     * snapshot and returns the total obtained marks.
     *
     * Expected answer format:
     * {
     *     "101": "B",
     *     "102": "A"
     * }
     *
     * The keys are question IDs and the values are selected option letters.
     */
    public int evaluate(
            QuestionPaperAudit audit,
            Map<String, String> answers) {

        // 1. Validate input.
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
        int matchedQuestions = 0;
        int correctQuestions = 0;

        List<Map<String, Object>> questions = audit.getQuestions();

        log.info(
                "Starting evaluation: auditId={}, questionCount={}, "
                        + "submittedAnswerCount={}",
                audit.getAuditId(),
                questions.size(),
                answers.size()
        );

        // 2. Evaluate each question in the saved audit snapshot.
        for (Map<String, Object> question : questions) {

            if (question == null) {
                log.error(
                        "Null question found in auditId={}",
                        audit.getAuditId()
                );
                continue;
            }

            Object idObject = question.get("questionId");
            Object correctObject = question.get("correctAnswer");
            Object marksObject = question.get("marks");

            // 3. Validate the stored evaluation data.
            if (idObject == null
                    || correctObject == null
                    || !(marksObject instanceof Number)) {

                log.error(
                        "Invalid evaluation data: auditId={}, question={}",
                        audit.getAuditId(),
                        question
                );

                throw new IllegalStateException(
                        "Invalid evaluation data for auditId: "
                                + audit.getAuditId()
                );
            }

            String questionId = String.valueOf(idObject).trim();
            String correctAnswer =
                    String.valueOf(correctObject).trim();

            int marks = ((Number) marksObject).intValue();

            if (questionId.isEmpty()
                    || correctAnswer.isEmpty()
                    || marks < 0) {

                throw new IllegalStateException(
                        "Invalid question ID, correct answer, or marks "
                                + "in auditId: " + audit.getAuditId()
                );
            }

            // 4. Get the candidate's answer for this question.
            String candidateAnswer = answers.get(questionId);

            // An unanswered question receives zero marks.
            if (candidateAnswer == null
                    || candidateAnswer.isBlank()) {

                log.info(
                        "Unanswered question: auditId={}, questionId={}",
                        audit.getAuditId(),
                        questionId
                );

                continue;
            }

            candidateAnswer = candidateAnswer.trim().toUpperCase();
            correctAnswer = correctAnswer.toUpperCase();

            matchedQuestions++;

            // 5. Compare the option letter with the saved correct letter.
            if (candidateAnswer.equals(correctAnswer)) {

                obtainedMarks = Math.addExact(obtainedMarks, marks);
                correctQuestions++;

                log.info(
                        "Correct answer: auditId={}, questionId={}, "
                                + "answer={}, marks={}",
                        audit.getAuditId(),
                        questionId,
                        candidateAnswer,
                        marks
                );

            } else {

                log.info(
                        "Incorrect answer: auditId={}, questionId={}, "
                                + "submitted={}, expected={}",
                        audit.getAuditId(),
                        questionId,
                        candidateAnswer,
                        correctAnswer
                );
            }
        }

        // 6. Log the final result.
        log.info(
                "Evaluation completed: auditId={}, obtainedMarks={}, "
                        + "answeredQuestions={}, correctQuestions={}, "
                        + "totalQuestions={}",
                audit.getAuditId(),
                obtainedMarks,
                matchedQuestions,
                correctQuestions,
                questions.size()
        );

        return obtainedMarks;
    }
}