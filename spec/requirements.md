# Requirements

Support Ticket Management System — functional and non-functional requirements.

## Overview

A web-based system enabling customers to submit support tickets, agents to manage and resolve them, and administrators to configure categories and monitor operations.

## Personas

| Persona | Description |
|---------|-------------|
| Customer | End user who creates tickets and tracks resolution |
| Agent | Support staff who triage, assign, and resolve tickets |
| Admin | Manages users, categories, SLA settings, and reports |

## Functional Requirements

### Ticket Lifecycle

| ID | Requirement | Priority |
|----|-------------|----------|
| REQ-001 | Customers can create a ticket with title, description, category, and priority | Must |
| REQ-002 | System assigns a unique ticket number on creation | Must |
| REQ-003 | New tickets start in `OPEN` status | Must |
| REQ-004 | Agents can view a queue of unassigned and assigned tickets | Must |
| REQ-005 | Agents can assign a ticket to themselves or another agent | Must |
| REQ-006 | Agents can update ticket status following defined transitions | Must |
| REQ-007 | Customers can view status and history of their own tickets | Must |
| REQ-008 | Agents and customers can add comments to a ticket | Must |
| REQ-009 | Internal comments are visible only to agents and admins | Should |
| REQ-010 | Agents can change ticket priority | Must |
| REQ-011 | Resolved tickets can be reopened by customers within 7 days | Should |
| REQ-012 | Closed tickets are read-only | Must |

### User Management

| ID | Requirement | Priority |
|----|-------------|----------|
| REQ-020 | Users authenticate with email and password | Must |
| REQ-021 | System supports roles: CUSTOMER, AGENT, ADMIN | Must |
| REQ-022 | Admins can create and deactivate agent accounts | Must |
| REQ-023 | Customers can register and manage their profile | Must |

### Categories and Configuration

| ID | Requirement | Priority |
|----|-------------|----------|
| REQ-030 | Admins can create, update, and deactivate ticket categories | Must |
| REQ-031 | Each category has a name and optional SLA response time (hours) | Should |
| REQ-032 | Deactivated categories cannot be selected for new tickets | Must |

### Search and Filtering

| ID | Requirement | Priority |
|----|-------------|----------|
| REQ-040 | Agents can filter tickets by status, priority, assignee, and category | Must |
| REQ-041 | Agents can search tickets by ticket number or title keyword | Must |
| REQ-042 | Customers can list their tickets with status filter | Must |

### Notifications

| ID | Requirement | Priority |
|----|-------------|----------|
| REQ-050 | Customers receive email when ticket status changes | Should |
| REQ-051 | Agents receive email when assigned a new ticket | Should |

### Reporting

| ID | Requirement | Priority |
|----|-------------|----------|
| REQ-060 | Admins can view ticket volume by status and category | Should |
| REQ-061 | Admins can view average resolution time per category | Could |

## Non-Functional Requirements

| ID | Requirement | Target |
|----|-------------|--------|
| NFR-001 | API response time (p95) | < 300ms for read, < 500ms for write |
| NFR-002 | Concurrent users | 500 simultaneous users |
| NFR-003 | Availability | 99.5% uptime |
| NFR-004 | Data retention | Tickets retained 3 years; soft-delete only |
| NFR-005 | Audit trail | All status changes and assignments logged |
| NFR-006 | Security | TLS 1.2+, OWASP Top 10 mitigations |
| NFR-007 | Accessibility | WCAG 2.1 AA for customer-facing UI |

## Acceptance Criteria Examples

### REQ-001: Create Ticket

**Given** an authenticated customer  
**When** they submit a ticket with title, description, category, and priority  
**Then** the system returns `201 Created` with ticket number, `OPEN` status, and timestamps  
**And** the ticket is visible in the customer's ticket list

### REQ-006: Status Transitions

**Given** a ticket in `IN_PROGRESS` status  
**When** an assigned agent marks it resolved  
**Then** status becomes `RESOLVED`  
**And** resolution timestamp is recorded  
**And** the customer is notified (if REQ-050 enabled)

## Out of Scope (v1)

- Live chat / real-time messaging
- Knowledge base / FAQ
- Multi-tenant organization support
- Mobile native apps
- Attachment uploads larger than 10 MB
- AI-assisted ticket routing

## Assumptions

- Single organization deployment
- Email delivery via external SMTP provider
- English-only UI and content in v1
- Business hours SLA calculation (Mon–Fri 09:00–17:00 UTC)

## Open Questions

| # | Question | Owner | Status |
|---|----------|-------|--------|
| OQ-001 | Should customers edit ticket description after creation? | PM | Open |
| OQ-002 | Maximum comment length? | PM | Open — propose 5000 chars |
