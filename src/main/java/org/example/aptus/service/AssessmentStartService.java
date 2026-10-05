
package org.example.aptus.service;

import org.example.aptus.config.AssessmentSettings;
import org.example.aptus.dto.AssessmentQuestionResponse;
import org.example.aptus.dto.StartAssessmentRequest;
import org.example.aptus.dto.StartAssessmentResponse;
import org.example.aptus.entity.Question;
import org.example.aptus.entity.QuestionPaperAudit;
import org.example.aptus.entity.QuestionPaperStatus;
import org.example.aptus.repository.QuestionPaperAuditRepository;
import org.example.aptus.repository.QuestionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssessmentStartService {

    private final QuestionRepository questionRepository;
    private final QuestionPaperAuditRepository auditRepository;
    private final AssessmentConfigurationService configurationService;

    public AssessmentStartService(
            QuestionRepository questionRepository,
            QuestionPaperAuditRepository auditRepository,
            AssessmentConfigurationService configurationService) {

        this.questionRepository = questionRepository;
        this.auditRepository = auditRepository;
        this.configurationService = configurationService;
    }

    @Transactional
    public StartAssessmentResponse startAssessment(
            StartAssessmentRequest request,
            Long adminId) {

        // 1. Validate the request.
        if (request == null) {
            throw new IllegalArgumentException(
                    "Assessment request cannot be null");
        }

        if (request.getCandidateId() == null) {
            throw new IllegalArgumentException(
                    "Candidate ID is required");
        }

        if (request.getCandidateName() == null
                || request.getCandidateName().isBlank()) {
            throw new IllegalArgumentException(
                    "Candidate name is required");
        }

        if (request.getLanguage() == null
                || request.getLanguage().isBlank()) {
            throw new IllegalArgumentException(
                    "Language is required");
        }

        if (request.getSource() == null
                || request.getSource().isBlank()) {
            throw new IllegalArgumentException(
                    "Source is required");
        }

        String language = request.getLanguage().trim();
        String source = request.getSource().trim();

        // 2. Get question count and duration from source configuration.
        AssessmentSettings settings =
                configurationService.getSettings(source);

        int totalQuestionsRequired = settings.getTotalQuestions();
        int durationMinutes = settings.getDurationMinutes();

        if (totalQuestionsRequired <= 0 || durationMinutes <= 0) {
            throw new IllegalStateException(
                    "Invalid assessment configuration for source: "
                            + source);
        }

        // 3. Fetch random questions for the requested language.
        List<Question> selectedQuestions =
                questionRepository.findRandomQuestionsByLanguage(
                        language,
                        totalQuestionsRequired);

        if (selectedQuestions.size() < totalQuestionsRequired) {
            throw new IllegalArgumentException(
                    "Not enough questions for language: " + language
                            + ". Required: " + totalQuestionsRequired
                            + ", Available: " + selectedQuestions.size());
        }

        List<Map<String, Object>> auditQuestions = new ArrayList<>();
        List<AssessmentQuestionResponse> responseQuestions =
                new ArrayList<>();

        int totalMarks = 0;

        // 4. Process the selected questions.
        for (Question question : selectedQuestions) {

            if (question.getQuestionData() == null) {
                throw new IllegalArgumentException(
                        "Question data is missing for question ID: "
                                + question.getQuestionId());
            }

            Object languageObject =
                    question.getQuestionData().get(language);

            if (!(languageObject instanceof Map<?, ?> languageData)) {
                throw new IllegalArgumentException(
                        "Question does not contain language: " + language
                                + ", Question ID: "
                                + question.getQuestionId());
            }

            Object questionObject = languageData.get("question");

            if (questionObject == null) {
                throw new IllegalArgumentException(
                        "Question text is missing for question ID: "
                                + question.getQuestionId());
            }

            String questionText = String.valueOf(questionObject);

            // Read options.
            Object optionsObject = languageData.get("options");

            if (!(optionsObject instanceof Map<?, ?> originalOptions)) {
                throw new IllegalArgumentException(
                        "Invalid options for question ID: "
                                + question.getQuestionId());
            }

            if (originalOptions.size() != 4) {
                throw new IllegalArgumentException(
                        "Expected exactly four options for question ID: "
                                + question.getQuestionId());
            }

            // Read the correct answer value from the question bank.
            Object correctAnswerObject =
                    languageData.get("correct_answer");

            if (correctAnswerObject == null) {
                throw new IllegalArgumentException(
                        "Correct answer is missing for question ID: "
                                + question.getQuestionId());
            }

            String originalCorrectAnswer =
                    String.valueOf(correctAnswerObject);

            List<String> optionValues = new ArrayList<>();

            for (Object value : originalOptions.values()) {
                if (value == null) {
                    throw new IllegalArgumentException(
                            "An option is null for question ID: "
                                    + question.getQuestionId());
                }

                optionValues.add(String.valueOf(value));
            }

            // Shuffle the option values.
            Collections.shuffle(optionValues);

            String[] optionKeys = {"A", "B", "C", "D"};

            Map<String, String> shuffledOptions =
                    new LinkedHashMap<>();

            String correctOption = null;

            for (int i = 0; i < optionValues.size(); i++) {

                String optionKey = optionKeys[i];
                String optionValue = optionValues.get(i);

                shuffledOptions.put(optionKey, optionValue);

                // Identify the correct option letter after shuffling.
                if (optionValue.equals(originalCorrectAnswer)) {

                    if (correctOption != null) {
                        throw new IllegalArgumentException(
                                "Duplicate option values make the correct "
                                        + "answer ambiguous for question ID: "
                                        + question.getQuestionId());
                    }

                    correctOption = optionKey;
                }
            }

            if (correctOption == null) {
                throw new IllegalArgumentException(
                        "Correct answer does not match any option for "
                                + "question ID: "
                                + question.getQuestionId());
            }

            // Calculate total marks.
            Integer marks = question.getMarks();

            if (marks == null || marks < 0) {
                throw new IllegalArgumentException(
                        "Invalid marks for question ID: "
                                + question.getQuestionId());
            }

            totalMarks = Math.addExact(totalMarks, marks);

            // 5. Save the complete question snapshot for evaluation.
            Map<String, Object> auditQuestion =
                    new LinkedHashMap<>();

            auditQuestion.put(
                    "questionId", question.getQuestionId());
            auditQuestion.put("question", questionText);
            auditQuestion.put("options", shuffledOptions);
            auditQuestion.put("correctAnswer", correctOption);
            auditQuestion.put("marks", marks);

            auditQuestions.add(auditQuestion);

            // 6. Prepare the candidate-facing response.
            AssessmentQuestionResponse questionResponse =
                    new AssessmentQuestionResponse();

            // questionId is Integer in this DTO.
            questionResponse.setQuestionId(
                    question.getQuestionId());

            // IMPORTANT: This line fixes the null question.
            questionResponse.setQuestion(questionText);

            questionResponse.setOptions(shuffledOptions);

            responseQuestions.add(questionResponse);
        }

        // 7. Record assessment timestamps.
        OffsetDateTime startTime = OffsetDateTime.now();
        OffsetDateTime endTime =
                startTime.plusMinutes(durationMinutes);

        LocalDateTime createdAt = startTime.toLocalDateTime();

        // 8. Create the audit entity.
        QuestionPaperAudit audit = new QuestionPaperAudit();

        audit.setAdminId(adminId);
        audit.setCandidateId(request.getCandidateId());
        audit.setCandidateName(request.getCandidateName());
        audit.setSource(source);
        audit.setQuestions(auditQuestions);

        audit.setTotalMarks(totalMarks);
        audit.setDurationMinutes(durationMinutes);

        audit.setCreatedAt(createdAt);
        audit.setStartTime(startTime);
        audit.setEndTime(endTime);

        audit.setObtainedMarks(0);
        audit.setFullscreenExitCount(0);
        audit.setTotalAwaySeconds(0L);
        audit.setSubmittedAt(null);
        audit.setStatus(QuestionPaperStatus.CREATED);

        // 9. Save the audit record.
        QuestionPaperAudit savedAudit =
                auditRepository.save(audit);

        // 10. Return audit ID and candidate-facing questions.
        StartAssessmentResponse response =
                new StartAssessmentResponse();

        response.setAuditId(savedAudit.getAuditId());
        response.setQuestions(responseQuestions);


        return response;
    }
}