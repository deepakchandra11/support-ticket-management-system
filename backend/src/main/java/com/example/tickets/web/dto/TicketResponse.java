package com.example.tickets.web.dto;

import com.example.tickets.domain.Ticket;
import com.example.tickets.domain.TicketPriority;
import com.example.tickets.domain.TicketStateMachine;
import com.example.tickets.domain.TicketStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String title,
        String description,
        TicketPriority priority,
        TicketStatus status,
        String assignee,
        Instant createdAt,
        Instant updatedAt,
        List<TicketStatus> allowedTransitions,
        List<CommentResponse> comments
) {
    public static TicketResponse from(Ticket ticket, List<CommentResponse> comments) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getAssignee(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                List.copyOf(TicketStateMachine.allowedTargets(ticket.getStatus())),
                comments
        );
    }
}
