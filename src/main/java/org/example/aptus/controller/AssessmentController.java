package org.example.aptus.controller;

import lombok.RequiredArgsConstructor;
import org.example.aptus.dto.GenerateAssessmentRequest;
import org.example.aptus.dto.GenerateAssessmentResponse;
import org.example.aptus.dto.SubmitTestRequest;
import org.example.aptus.dto.SubmitTestResponse;
import org.example.aptus.service.AssessmentGenerationService;
import org.example.aptus.service.LanguageService;
import org.example.aptus.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentGenerationService assessmentGenerationService;
    private final TestService testService;
    private final LanguageService languageService;

    @PostMapping("/generate")
    public ResponseEntity<GenerateAssessmentResponse> generateAssessment(
            @RequestBody GenerateAssessmentRequest request) {

        return ResponseEntity
                .status(201)
                .body(assessmentGenerationService.startAssessment(request, null));
    }

    @PostMapping("/{auditId}/submit")
    public ResponseEntity<SubmitTestResponse> submitAssessment(
            @PathVariable Long auditId,
            @RequestBody SubmitTestRequest request) {

        return ResponseEntity.ok(
                testService.submitTest(
                        auditId,
                        request.getAnswers()
                )
        );
    }

    @GetMapping("/languages")
    public ResponseEntity<?> getAssessmentLanguages() {

        return ResponseEntity.ok(
                languageService.getAvailableLanguages()
        );
    }
}