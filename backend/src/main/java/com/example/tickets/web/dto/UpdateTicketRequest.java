package com.example.tickets.web.dto;

import com.example.tickets.domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTicketRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 10000, message = "Description must be at most 10000 characters")
        String description,

        @NotNull(message = "Priority is required")
        TicketPriority priority,

        @Size(max = 200, message = "Assignee must be at most 200 characters")
        String assignee
) {
}
