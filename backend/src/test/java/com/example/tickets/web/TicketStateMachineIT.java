package com.example.tickets.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketStateMachineIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void walksHappyPathFromOpenToClosed() throws Exception {
        String id = createTicket("Printer jam", "Paper is stuck", "HIGH");

        transition(id, "IN_PROGRESS");
        transition(id, "RESOLVED");
        mockMvc.perform(post("/api/v1/tickets/{id}/transitions", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetStatus\":\"CLOSED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"))
                .andExpect(jsonPath("$.allowedTransitions").isEmpty());
    }

    @Test
    void cancelsFromOpenAndFromInProgress() throws Exception {
        String openId = createTicket("Cancel me", "No longer needed", "LOW");
        mockMvc.perform(post("/api/v1/tickets/{id}/transitions", openId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetStatus\":\"CANCELLED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        String workingId = createTicket("Stop work", "Duplicate", "MEDIUM");
        transition(workingId, "IN_PROGRESS");
        mockMvc.perform(post("/api/v1/tickets/{id}/transitions", workingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetStatus\":\"CANCELLED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void rejectsClosedResolvedAndCancelledBackToOpen() throws Exception {
        String closedId = createTicket("Already closed", "Done", "LOW");
        transition(closedId, "IN_PROGRESS");
        transition(closedId, "RESOLVED");
        transition(closedId, "CLOSED");
        expectRejected(closedId, "OPEN", "CLOSED");

        String resolvedId = createTicket("Resolved ticket", "Waiting to close", "MEDIUM");
        transition(resolvedId, "IN_PROGRESS");
        transition(resolvedId, "RESOLVED");
        expectRejected(resolvedId, "OPEN", "RESOLVED");

        String cancelledId = createTicket("Cancelled ticket", "Withdrawn", "LOW");
        transition(cancelledId, "CANCELLED");
        expectRejected(cancelledId, "OPEN", "CANCELLED");
    }

    @Test
    void rejectsValidationErrors() throws Exception {
        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","description":"","priority":"LOW"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.violations").isArray());
    }

    @Test
    void updatesFieldsAddsCommentsAndFiltersByStatusAndKeyword() throws Exception {
        String id = createTicket("VPN outage", "Users cannot connect", "URGENT");
        createTicket("Billing question", "Invoice looks wrong", "LOW");

        mockMvc.perform(put("/api/v1/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"VPN outage in HQ",
                                  "description":"Users cannot connect from the office",
                                  "priority":"HIGH",
                                  "assignee":"Alex Agent"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("VPN outage in HQ"))
                .andExpect(jsonPath("$.assignee").value("Alex Agent"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"));

        mockMvc.perform(post("/api/v1/tickets/{id}/comments", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"body":"Checking the gateway now","author":"Alex Agent"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.body").value("Checking the gateway now"));

        mockMvc.perform(get("/api/v1/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].author").value("Alex Agent"));

        mockMvc.perform(get("/api/v1/tickets").param("q", "HQ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(id));

        transition(id, "IN_PROGRESS");
        mockMvc.perform(get("/api/v1/tickets").param("status", "IN_PROGRESS").param("q", "VPN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("IN_PROGRESS"));
    }

    private String createTicket(String title, String description, String priority) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s","description":"%s","priority":"%s"}
                                """.formatted(title, description, priority)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asText();
    }

    private void transition(String id, String targetStatus) throws Exception {
        mockMvc.perform(post("/api/v1/tickets/{id}/transitions", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetStatus\":\"" + targetStatus + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(targetStatus));
    }

    private void expectRejected(String id, String targetStatus, String from) throws Exception {
        mockMvc.perform(post("/api/v1/tickets/{id}/transitions", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetStatus\":\"" + targetStatus + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Invalid State Transition"))
                .andExpect(jsonPath("$.detail").value("Cannot transition from " + from + " to " + targetStatus + "."));
    }
}
