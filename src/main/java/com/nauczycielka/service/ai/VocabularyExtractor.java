package com.nauczycielka.service.ai;

import com.nauczycielka.dto.ai.OllamaRequest;
import com.nauczycielka.dto.ai.OllamaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class VocabularyExtractor {

    private final OllamaClient ollamaClient;

    public VocabularyExtractor(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public Set<String> extractVocabulary(String transcript) {
        String prompt = String.format(
                "Extract ONLY unique technical terms, domain-specific words, and important concepts from the following lecture transcript. " +
                "Return them as a comma-separated list with NO explanations. " +
                "Only return the words, nothing else.\n\nTranscript:\n%s",
                transcript
        );

        try {
            OllamaRequest request = new OllamaRequest("qwen2", prompt, false);
            OllamaResponse response = ollamaClient.generate(request);

            return parseVocabularyResponse(response.response());
        } catch (Exception e) {
            log.error("Error extracting vocabulary from transcript", e);
            return new HashSet<>();
        }
    }

    private Set<String> parseVocabularyResponse(String response) {
        Set<String> vocabulary = new HashSet<>();

        // Split by comma and clean up
        String[] terms = response.split(",");
        for (String term : terms) {
            String cleaned = term.trim()
                    .replaceAll("^[\\d\\.\\s]+", "")  // Remove leading numbers and dots
                    .replaceAll("[^a-zA-Z0-9\\s\\-]", "")  // Remove special characters
                    .trim();

            if (!cleaned.isEmpty() && cleaned.length() > 2) {
                vocabulary.add(cleaned.toLowerCase());
            }
        }

        return vocabulary;
    }
}
