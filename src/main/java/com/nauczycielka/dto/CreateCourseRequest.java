package com.nauczycielka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateCourseRequest(
        @NotBlank(message = "Title is required")
        @JsonProperty("title")
        String title,

        @JsonProperty("description")
        String description,

        @JsonProperty("instructor_id")
        Long instructorId
) {
}
