package com.nauczycielka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TranscriptRequest(
        @NotBlank(message = "Content is required")
        @JsonProperty("content")
        String content,

        @JsonProperty("speaker")
        String speaker
) {
}
