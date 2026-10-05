package org.example.aptus.service;

import lombok.RequiredArgsConstructor;
import org.example.aptus.dto.LanguageResponse;
import org.example.aptus.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final QuestionRepository questionRepository;

    /**
     * Retrieves all distinct language names available
     * in the questions_db question_data JSONB column.
     */
    @Transactional(readOnly = true)
    public LanguageResponse getAvailableLanguages() {

        List<String> languages =
                questionRepository.findAvailableLanguages();

        return new LanguageResponse(languages);
    }
}