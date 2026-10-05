package org.example.aptus.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AssessmentSettings {

    private final int totalQuestions;
    private final int durationMinutes;
}