package com.example.tickets.domain;

import com.example.tickets.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketStateMachineTest {

    @ParameterizedTest
    @CsvSource({
            "OPEN, IN_PROGRESS, true",
            "OPEN, CANCELLED, true",
            "IN_PROGRESS, RESOLVED, true",
            "IN_PROGRESS, CANCELLED, true",
            "RESOLVED, CLOSED, true",
            "CLOSED, OPEN, false",
            "RESOLVED, OPEN, false",
            "CANCELLED, OPEN, false",
            "OPEN, RESOLVED, false",
            "OPEN, CLOSED, false",
            "RESOLVED, CANCELLED, false",
            "CLOSED, CANCELLED, false",
            "CANCELLED, IN_PROGRESS, false"
    })
    void allowsOnlyDefinedTransitions(TicketStatus from, TicketStatus to, boolean allowed) {
        assertThat(TicketStateMachine.canTransition(from, to)).isEqualTo(allowed);
    }

    @Test
    void rejectsInvalidTransition() {
        assertThatThrownBy(() -> TicketStateMachine.assertTransition(TicketStatus.CLOSED, TicketStatus.OPEN))
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasMessage("Cannot transition from CLOSED to OPEN.");
    }
}
