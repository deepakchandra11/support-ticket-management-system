package com.example.tickets.exception;

import com.example.tickets.domain.TicketStatus;

public class InvalidStateTransitionException extends RuntimeException {

    private final TicketStatus from;
    private final TicketStatus to;

    public InvalidStateTransitionException(TicketStatus from, TicketStatus to) {
        super("Cannot transition from " + from + " to " + to + ".");
        this.from = from;
        this.to = to;
    }

    public TicketStatus getFrom() {
        return from;
    }

    public TicketStatus getTo() {
        return to;
    }
}
