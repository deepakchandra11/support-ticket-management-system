package com.example.tickets.exception;

import java.util.UUID;

public class TicketNotFoundException extends RuntimeException {

    private final UUID ticketId;

    public TicketNotFoundException(UUID ticketId) {
        super("Ticket with id '" + ticketId + "' does not exist.");
        this.ticketId = ticketId;
    }

    public UUID getTicketId() {
        return ticketId;
    }
}
