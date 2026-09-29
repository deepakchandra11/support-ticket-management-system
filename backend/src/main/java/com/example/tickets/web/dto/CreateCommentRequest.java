package com.example.tickets.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Comment is required")
        @Size(max = 5000, message = "Comment must be at most 5000 characters")
        String body,

        @NotBlank(message = "Author is required")
        @Size(max = 200, message = "Author must be at most 200 characters")
        String author
) {
}
