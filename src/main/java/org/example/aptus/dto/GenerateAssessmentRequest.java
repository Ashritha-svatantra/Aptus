package org.example.aptus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GenerateAssessmentRequest {

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotBlank(message = "Candidate name is required")
    private String candidateName;

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Source is required")
    private String source;
}