package com.nauczycielka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateCourseRequest(
        @JsonProperty("title")
        String title,

        @JsonProperty("description")
        String description
) {
}
