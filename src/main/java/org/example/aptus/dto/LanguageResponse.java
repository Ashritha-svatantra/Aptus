package org.example.aptus.dto;

import java.util.List;

public class LanguageResponse {

    private List<String> languages;

    public LanguageResponse() {
    }

    public LanguageResponse(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
}