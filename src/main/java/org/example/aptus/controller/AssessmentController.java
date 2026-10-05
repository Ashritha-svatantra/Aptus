package org.example.aptus.controller;

import org.example.aptus.dto.StartAssessmentRequest;
import org.example.aptus.dto.SubmitTestRequest;
import org.example.aptus.dto.SubmitTestResponse;
import org.example.aptus.service.AssessmentStartService;
import org.example.aptus.service.TestService;
import org.example.aptus.service.LanguageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentController {

    private final AssessmentStartService assessmentStartService;
    private final TestService testService;
    private final LanguageService languageService;

    // Constructor-based dependency injection
    public AssessmentController(
            AssessmentStartService assessmentStartService,
            TestService testService,
            LanguageService languageService) {

        this.assessmentStartService = assessmentStartService;
        this.testService = testService;
        this.languageService = languageService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateAssessment(
            @RequestBody StartAssessmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assessmentStartService.startAssessment(request, null));
    }

    // 2. Submit an assessment

    @PostMapping("/{auditId}/submit")
    public ResponseEntity<SubmitTestResponse> submitAssessment(
            @PathVariable Long auditId,
            @RequestBody SubmitTestRequest request) {

        SubmitTestResponse response = testService.submitTest(
                auditId,
                request.getAnswers()
        );

        return ResponseEntity.ok(response);
    }



    // 3. Get available assessment languages
    @GetMapping("/languages")
    public ResponseEntity<?> getAssessmentLanguages() {

        return ResponseEntity.ok(
                languageService.getAvailableLanguages()
        );
    }
}

