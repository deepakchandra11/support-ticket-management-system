package com.example.tickets.web.controller;

import com.example.tickets.domain.TicketStatus;
import com.example.tickets.service.TicketService;
import com.example.tickets.web.dto.CommentResponse;
import com.example.tickets.web.dto.CreateCommentRequest;
import com.example.tickets.web.dto.CreateTicketRequest;
import com.example.tickets.web.dto.TicketListResponse;
import com.example.tickets.web.dto.TicketResponse;
import com.example.tickets.web.dto.TransitionRequest;
import com.example.tickets.web.dto.UpdateTicketRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.create(request);
    }

    @GetMapping
    public TicketListResponse list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String q
    ) {
        return new TicketListResponse(ticketService.list(status, q));
    }

    @GetMapping("/{ticketId}")
    public TicketResponse get(@PathVariable UUID ticketId) {
        return ticketService.get(ticketId);
    }

    @PutMapping("/{ticketId}")
    public TicketResponse update(@PathVariable UUID ticketId, @Valid @RequestBody UpdateTicketRequest request) {
        return ticketService.update(ticketId, request);
    }

    @PostMapping("/{ticketId}/transitions")
    public TicketResponse transition(@PathVariable UUID ticketId, @Valid @RequestBody TransitionRequest request) {
        return ticketService.transition(ticketId, request.targetStatus());
    }

    @PostMapping("/{ticketId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable UUID ticketId, @Valid @RequestBody CreateCommentRequest request) {
        return ticketService.addComment(ticketId, request);
    }
}
