package org.example.aptus.config;

public class AssessmentSettings {

    private final int totalQuestions;
    private final int durationMinutes;

    public AssessmentSettings(
            int totalQuestions,
            int durationMinutes) {
        this.totalQuestions = totalQuestions;
        this.durationMinutes = durationMinutes;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

}