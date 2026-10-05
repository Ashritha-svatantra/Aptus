package org.example.aptus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateAssessmentResponse {

    private Long auditId;
    private List<AssessmentQuestionResponse> questions;
}