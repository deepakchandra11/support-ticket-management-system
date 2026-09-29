# Java Spring Boot Guidelines

Project conventions for the Support Ticket Management System backend.

**Location**: All backend code lives in [`backend/`](../backend/) at the repository root. Shared specs are in [`spec/`](../spec/).

## Stack

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- Spring Validation
- Gradle (multi-module when the codebase grows)

## Package Structure

```
com.example.tickets
├── config/          # Security, OpenAPI, Jackson, async
├── domain/          # JPA entities and domain enums
├── repository/      # Spring Data repositories
├── service/         # Business logic and orchestration
├── web/             # REST controllers, request/response DTOs
│   ├── controller/
│   ├── dto/
│   └── mapper/
├── exception/       # Custom exceptions and @ControllerAdvice
└── util/            # Shared helpers (keep minimal)
```

## Layering Rules

1. **Controllers** are thin: validate input, delegate to services, map responses.
2. **Services** own business rules, transactions, and state transitions.
3. **Repositories** contain query logic only; no business rules.
4. **Entities** reflect persistence; never expose entities directly from controllers.
5. **DTOs** are used at API boundaries; map with dedicated mappers (MapStruct preferred).

## Spring Boot Conventions

- Use constructor injection (`@RequiredArgsConstructor` or explicit constructors). Avoid field injection.
- Annotate service methods with `@Transactional` at the service layer, not controllers.
- Use `record` types for immutable DTOs where practical.
- Prefer `Optional` internally; never return bare `null` from public service methods.
- Externalize configuration via `application.properties` and environment variables.
- Use `@ConfigurationProperties` for grouped settings (pagination defaults, SLA thresholds).

## Error Handling

- Throw domain-specific exceptions (`TicketNotFoundException`, `InvalidStateTransitionException`).
- Handle all exceptions in a single `@RestControllerAdvice`.
- Return RFC 7807 Problem Details (`application/problem+json`) for API errors.
- Log at `WARN` for client errors (4xx) and `ERROR` for server errors (5xx) with correlation IDs.

## Security

- Authenticate all endpoints except health checks and OpenAPI docs (in non-prod).
- Enforce role-based access: `CUSTOMER`, `AGENT`, `ADMIN`.
- Scope ticket access: customers see only their tickets; agents see assigned or team queue.
- Never trust client-supplied user IDs; derive identity from the security context.

## Persistence

- Use UUID primary keys for externally exposed resources.
- Add `createdAt`, `updatedAt`, and `version` (optimistic locking) on aggregate roots.
- Index foreign keys and frequently filtered columns (`status`, `assignee_id`, `created_at`).
- Use Flyway for schema migrations; one migration per logical change.

## API Integration

- Follow [api-standards.md](api-standards.md) for REST design.
- Version the API under `/api/v1`.
- Publish OpenAPI 3.1 spec generated from code annotations or maintained alongside `spec/api-contract.md`.

## Code Quality

- Keep methods under 30 lines; extract private helpers when logic branches grow.
- No business logic in `@Entity` lifecycle callbacks.
- Write self-documenting names; add JavaDoc only for non-obvious domain rules.
- Run `./gradlew check` before opening a PR.

## Dependencies

- Do not add libraries without justification in the PR description.
- Prefer Spring-managed starters over ad-hoc HTTP/database clients.
- Pin dependency versions through the Spring Boot Gradle plugin and dependency management.
