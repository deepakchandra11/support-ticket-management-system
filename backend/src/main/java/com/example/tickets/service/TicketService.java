package com.example.tickets.service;

import com.example.tickets.domain.Comment;
import com.example.tickets.domain.Ticket;
import com.example.tickets.domain.TicketStatus;
import com.example.tickets.exception.TicketNotFoundException;
import com.example.tickets.repository.CommentRepository;
import com.example.tickets.repository.TicketRepository;
import com.example.tickets.web.dto.CommentResponse;
import com.example.tickets.web.dto.CreateCommentRequest;
import com.example.tickets.web.dto.CreateTicketRequest;
import com.example.tickets.web.dto.TicketResponse;
import com.example.tickets.web.dto.UpdateTicketRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;

    public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public TicketResponse create(CreateTicketRequest request) {
        Instant now = Instant.now();
        Ticket ticket = new Ticket(
                UUID.randomUUID(),
                request.title().trim(),
                request.description().trim(),
                request.priority(),
                blankToNull(request.assignee()),
                now
        );
        return toDetail(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> list(TicketStatus status, String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        return ticketRepository.search(status, normalizedKeyword).stream()
                .map(ticket -> TicketResponse.from(ticket, List.of()))
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse get(UUID id) {
        return toDetail(findTicket(id));
    }

    @Transactional
    public TicketResponse update(UUID id, UpdateTicketRequest request) {
        Ticket ticket = findTicket(id);
        ticket.update(
                request.title().trim(),
                request.description().trim(),
                request.priority(),
                blankToNull(request.assignee()),
                Instant.now()
        );
        return toDetail(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponse transition(UUID id, TicketStatus targetStatus) {
        Ticket ticket = findTicket(id);
        ticket.transitionTo(targetStatus, Instant.now());
        return toDetail(ticketRepository.save(ticket));
    }

    @Transactional
    public CommentResponse addComment(UUID id, CreateCommentRequest request) {
        Ticket ticket = findTicket(id);
        Comment comment = new Comment(
                UUID.randomUUID(),
                ticket,
                request.body().trim(),
                request.author().trim(),
                Instant.now()
        );
        return CommentResponse.from(commentRepository.save(comment));
    }

    private Ticket findTicket(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    private TicketResponse toDetail(Ticket ticket) {
        List<CommentResponse> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId()).stream()
                .map(CommentResponse::from)
                .toList();
        return TicketResponse.from(ticket, comments);
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
