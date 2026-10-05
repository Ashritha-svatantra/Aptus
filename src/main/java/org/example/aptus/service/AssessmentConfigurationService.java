package org.example.aptus.service;

import org.example.aptus.config.AssessmentSettings;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class AssessmentConfigurationService {

    private final Map<String, AssessmentSettings> configurations =
            Map.of(
                    "TIGERLEAP",
                    new AssessmentSettings(20, 30)
            );

    public AssessmentSettings getSettings(String source) {

        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException(
                    "Assessment source is required"
            );
        }

        String normalizedSource =
                source.trim().toUpperCase(Locale.ROOT);

        AssessmentSettings settings =
                configurations.get(normalizedSource);

        if (settings == null) {
            throw new IllegalArgumentException(
                    "Unsupported assessment source: " + source
            );
        }

        return settings;
    }
}