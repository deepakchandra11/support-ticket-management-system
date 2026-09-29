package com.example.tickets.web.dto;

import com.example.tickets.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record TransitionRequest(
        @NotNull(message = "Target status is required")
        TicketStatus targetStatus
) {
}
