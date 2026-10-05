
package org.example.aptus.dto;

import java.util.Map;

public class AssessmentQuestionResponse {

    private Integer questionId;
    private String question;
    private Map<String, String> options;

    public AssessmentQuestionResponse() {
    }

    public AssessmentQuestionResponse(
            Integer questionId,
            String question,
            Map<String, String> options) {
        this.questionId = questionId;
        this.question = question;
        this.options = options;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}