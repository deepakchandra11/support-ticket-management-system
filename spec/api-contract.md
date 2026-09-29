# API Contract

REST API specification for the Support Ticket Management System.

Base URL: `/api/v1`

Authentication: `Authorization: Bearer <jwt>` on all endpoints unless noted.

---

## Auth

### POST /auth/register

**Auth**: None

**Request**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "fullName": "Jane Customer"
}
```

**Response** `201 Created`
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "fullName": "Jane Customer",
  "role": "CUSTOMER"
}
```

### POST /auth/login

**Auth**: None

**Request**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response** `200 OK`
```json
{
  "accessToken": "jwt",
  "refreshToken": "jwt",
  "expiresIn": 3600
}
```

---

## Tickets

### POST /tickets

**Auth**: CUSTOMER, AGENT, ADMIN  
**Requirement**: REQ-001, REQ-002, REQ-003

**Request**
```json
{
  "title": "Cannot reset password",
  "description": "The reset link expires immediately.",
  "categoryId": "uuid",
  "priority": "MEDIUM"
}
```

**Response** `201 Created`
```json
{
  "id": "uuid",
  "ticketNumber": "TKT-2026-00042",
  "title": "Cannot reset password",
  "description": "The reset link expires immediately.",
  "status": "OPEN",
  "priority": "MEDIUM",
  "category": { "id": "uuid", "name": "Technical" },
  "customer": { "id": "uuid", "fullName": "Jane Customer" },
  "assignee": null,
  "createdAt": "2026-09-27T08:00:00Z",
  "updatedAt": "2026-09-27T08:00:00Z"
}
```

**Errors**: 400 (validation), 404 (category not found)

---

### GET /tickets

**Auth**: CUSTOMER (own tickets), AGENT, ADMIN (all)  
**Requirement**: REQ-040, REQ-042

**Query Parameters**

| Param | Type | Description |
|-------|------|-------------|
| status | enum | Filter by status |
| priority | enum | Filter by priority |
| assigneeId | uuid | Filter by assignee |
| categoryId | uuid | Filter by category |
| q | string | Search ticket number or title |
| page | int | Page number (0-based) |
| size | int | Page size (default 20) |
| sort | string | e.g., `createdAt,desc` |

**Response** `200 OK`
```json
{
  "data": [ /* Ticket summary objects */ ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 42,
    "totalPages": 3
  }
}
```

---

### GET /tickets/{ticketId}

**Auth**: CUSTOMER (own), AGENT, ADMIN  
**Requirement**: REQ-007

**Response** `200 OK` — Full ticket object with recent comments.

**Errors**: 403 (not owner), 404

---

### PATCH /tickets/{ticketId}

**Auth**: AGENT, ADMIN

Partial update for priority and category.

**Request**
```json
{
  "priority": "HIGH",
  "categoryId": "uuid"
}
```

**Response** `200 OK` — Updated ticket.

**Errors**: 400, 404, 409 (closed ticket)

---

### POST /tickets/{ticketId}/assign

**Auth**: AGENT, ADMIN  
**Requirement**: REQ-005

**Request**
```json
{
  "assigneeId": "uuid"
}
```

**Response** `200 OK` — Updated ticket with assignee.

**Errors**: 404 (ticket or assignee), 409 (invalid status)

---

### POST /tickets/{ticketId}/transitions

**Auth**: Role-dependent (see state-machine.md)  
**Requirement**: REQ-006

**Request**
```json
{
  "targetStatus": "IN_PROGRESS"
}
```

**Response** `200 OK` — Updated ticket.

**Errors**: 403, 404, 409 (invalid transition)

---

### GET /tickets/{ticketId}/history

**Auth**: CUSTOMER (own, public events only), AGENT, ADMIN

**Response** `200 OK`
```json
{
  "data": [
    {
      "eventType": "STATUS_CHANGED",
      "actor": { "id": "uuid", "fullName": "Agent Smith" },
      "payload": { "from": "OPEN", "to": "IN_PROGRESS" },
      "createdAt": "2026-09-27T09:00:00Z"
    }
  ]
}
```

---

## Comments

### POST /tickets/{ticketId}/comments

**Auth**: CUSTOMER (own ticket), AGENT, ADMIN  
**Requirement**: REQ-008, REQ-009

**Request**
```json
{
  "body": "I tried clearing cache but issue persists.",
  "internal": false
}
```

**Response** `201 Created`
```json
{
  "id": "uuid",
  "body": "I tried clearing cache but issue persists.",
  "internal": false,
  "author": { "id": "uuid", "fullName": "Jane Customer" },
  "createdAt": "2026-09-27T10:00:00Z"
}
```

**Errors**: 400, 403, 404, 409 (closed ticket)

---

### GET /tickets/{ticketId}/comments

**Auth**: CUSTOMER (excludes internal), AGENT, ADMIN

**Response** `200 OK` — Paginated comment list.

---

## Categories

### GET /categories

**Auth**: Any authenticated user

**Response** `200 OK`
```json
{
  "data": [
    {
      "id": "uuid",
      "name": "Technical",
      "description": "Product and technical issues",
      "slaResponseHours": 24
    }
  ]
}
```

### POST /categories

**Auth**: ADMIN  
**Requirement**: REQ-030

**Request**
```json
{
  "name": "Billing",
  "description": "Payment and invoice issues",
  "slaResponseHours": 48
}
```

**Response** `201 Created`

### PATCH /categories/{categoryId}

**Auth**: ADMIN

**Request**
```json
{
  "name": "Billing & Payments",
  "active": false
}
```

**Response** `200 OK`

---

## Users (Admin)

### GET /users

**Auth**: ADMIN

**Query**: `role`, `active`, `page`, `size`

**Response** `200 OK` — Paginated user list (no password fields).

### POST /users

**Auth**: ADMIN  
**Requirement**: REQ-022

**Request**
```json
{
  "email": "agent@example.com",
  "password": "SecurePass123!",
  "fullName": "Agent Smith",
  "role": "AGENT"
}
```

**Response** `201 Created`

### PATCH /users/{userId}

**Auth**: ADMIN

**Request**
```json
{
  "active": false
}
```

**Response** `200 OK`

---

## Reports

### GET /reports/ticket-summary

**Auth**: ADMIN  
**Requirement**: REQ-060

**Query**: `from` (date), `to` (date)

**Response** `200 OK`
```json
{
  "byStatus": { "OPEN": 12, "IN_PROGRESS": 8, "RESOLVED": 45, "CLOSED": 120 },
  "byCategory": [
    { "categoryName": "Technical", "count": 80 },
    { "categoryName": "Billing", "count": 35 }
  ]
}
```

---

## Common Error Response

```json
{
  "type": "https://api.example.com/problems/invalid-state-transition",
  "title": "Invalid State Transition",
  "status": 409,
  "detail": "Cannot transition from CLOSED to IN_PROGRESS.",
  "instance": "/api/v1/tickets/uuid/transitions",
  "correlationId": "req-abc123"
}
```

## Endpoint Summary

| Method | Path | Roles | Requirement |
|--------|------|-------|-------------|
| POST | /auth/register | — | REQ-023 |
| POST | /auth/login | — | REQ-020 |
| POST | /tickets | ALL | REQ-001 |
| GET | /tickets | ALL | REQ-040, REQ-042 |
| GET | /tickets/{id} | ALL | REQ-007 |
| PATCH | /tickets/{id} | AGENT, ADMIN | REQ-010 |
| POST | /tickets/{id}/assign | AGENT, ADMIN | REQ-005 |
| POST | /tickets/{id}/transitions | Role-based | REQ-006 |
| GET | /tickets/{id}/history | ALL | NFR-005 |
| POST | /tickets/{id}/comments | ALL | REQ-008 |
| GET | /tickets/{id}/comments | ALL | REQ-007 |
| GET | /categories | ALL | REQ-030 |
| POST | /categories | ADMIN | REQ-030 |
| PATCH | /categories/{id} | ADMIN | REQ-030 |
| GET | /users | ADMIN | REQ-022 |
| POST | /users | ADMIN | REQ-022 |
| GET | /reports/ticket-summary | ADMIN | REQ-060 |
