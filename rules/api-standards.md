# API Standards

REST API conventions for the Support Ticket Management System.

## Base URL and Versioning

- Base path: `/api/v1`
- Version in URL path, not headers.
- Breaking changes require `/api/v2`; maintain v1 until deprecation window ends.

## Resource Naming

- Use plural nouns: `/tickets`, `/comments`, `/users`.
- Use kebab-case for multi-word path segments: `/ticket-categories`.
- Nest sub-resources shallowly: `/tickets/{ticketId}/comments`.
- Maximum nesting depth: 2 levels.

## HTTP Methods

| Method | Usage |
|--------|-------|
| GET | Retrieve resource(s); safe and idempotent |
| POST | Create resource or trigger action (`/tickets/{id}/assign`) |
| PUT | Full replacement of a resource |
| PATCH | Partial update |
| DELETE | Remove resource (soft-delete preferred for tickets) |

## Status Codes

| Code | When |
|------|------|
| 200 | Successful GET, PUT, PATCH |
| 201 | Successful POST creating a resource |
| 204 | Successful DELETE with no body |
| 400 | Validation error, malformed request |
| 401 | Missing or invalid authentication |
| 403 | Authenticated but not authorized |
| 404 | Resource not found |
| 409 | Conflict (invalid state transition, duplicate) |
| 422 | Semantically invalid (business rule violation) |
| 500 | Unexpected server error |

## Request and Response Format

- `Content-Type: application/json`
- `Accept: application/json`
- Use camelCase for JSON property names.
- ISO-8601 UTC timestamps: `2026-09-27T08:00:00Z`.
- UUIDs as strings for all public identifiers.

## Pagination

List endpoints return:

```json
{
  "data": [],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 142,
    "totalPages": 8
  }
}
```

- Query params: `page` (0-based), `size` (default 20, max 100).
- Sorting: `sort=createdAt,desc` (repeatable).

## Filtering

- Use query parameters: `?status=OPEN&priority=HIGH&assigneeId={uuid}`.
- Document all supported filters in `spec/api-contract.md`.

## Error Responses

Use RFC 7807 Problem Details:

```json
{
  "type": "https://api.example.com/problems/ticket-not-found",
  "title": "Ticket Not Found",
  "status": 404,
  "detail": "Ticket with id 'abc-123' does not exist.",
  "instance": "/api/v1/tickets/abc-123",
  "correlationId": "req-7f3a2b1c"
}
```

- `type` is a stable URI identifying the problem category.
- Include `correlationId` on every error for support tracing.
- Validation errors add a `violations` array with `field` and `message`.

## Authentication

- Bearer JWT in `Authorization` header.
- Token claims: `sub` (user ID), `roles` (array).
- Refresh token flow documented separately; not part of ticket API surface.

## Idempotency

- POST actions that allocate resources accept optional `Idempotency-Key` header.
- Server stores key + response for 24 hours; duplicate keys return the original response.

## Documentation

- Maintain OpenAPI 3.1 spec at `docs/openapi.yaml` (generated or hand-synced).
- `spec/api-contract.md` is the source of truth for review; OpenAPI must stay aligned.
- Every endpoint documents: auth requirement, request schema, response schema, error cases.

## Backward Compatibility

- Do not remove fields without deprecation notice.
- Do not change field types or semantics in-place.
- Add optional fields freely; new required fields require a new API version.
