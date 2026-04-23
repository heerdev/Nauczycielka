package com.nauczycielka.service.ai;

import com.nauczycielka.dto.ai.OllamaRequest;
import com.nauczycielka.dto.ai.OllamaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SummaryGenerator {

    private final OllamaClient ollamaClient;

    public SummaryGenerator(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public String generateSummary(String transcript) {
        String prompt = String.format(
                "Create a concise summary of the following lecture in 3-5 key points. " +
                "Be clear and educational:\n\nTranscript:\n%s",
                transcript
        );

        try {
            OllamaRequest request = new OllamaRequest("qwen2", prompt, false);
            OllamaResponse response = ollamaClient.generate(request);

            return response.response().trim();
        } catch (Exception e) {
            log.error("Error generating summary from transcript", e);
            return "Unable to generate summary at this time.";
        }
    }
}
