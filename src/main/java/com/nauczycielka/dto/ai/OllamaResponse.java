package com.nauczycielka.dto.ai;

public record OllamaResponse(
        String response,
        String model,
        long created_at,
        boolean done
) {
}
