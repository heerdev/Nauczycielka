package com.nauczycielka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull(message = "Student ID is required")
        @JsonProperty("student_id")
        Long studentId
) {
}
