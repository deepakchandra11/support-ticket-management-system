# Testing Guidelines

Testing standards for the Support Ticket Management System.

## Test Pyramid

| Layer | Scope | Tools | Target |
|-------|-------|-------|--------|
| Unit | Services, mappers, validators, state machine | JUnit 5, Mockito, AssertJ | Fast, no Spring context |
| Integration | Repositories, controllers, security | `@SpringBootTest`, Testcontainers (PostgreSQL) | Real DB, mocked externals |
| Contract | API shape and error responses | Spring MockMvc or REST Assured | Against `spec/api-contract.md` |
| E2E | Critical user journeys | Optional (Playwright/Cypress for UI) | Smoke paths only |

## Naming Conventions

- Test classes: `{ClassName}Test` (unit), `{ClassName}IT` (integration).
- Test methods: `should{ExpectedBehavior}_when{Condition}`.
- Test data builders live in `src/test/java/.../fixtures/`.

## Unit Tests

- Test one behavior per test method.
- Use `@ExtendWith(MockitoExtension.class)` for pure unit tests.
- Mock only direct dependencies; verify interactions when behavior depends on them.
- Cover happy path, validation failures, authorization denials, and state transition edge cases.

## Integration Tests

- Use Testcontainers for PostgreSQL; do not rely on H2 for integration tests.
- Use `@Transactional` rollback or explicit cleanup per test class strategy—pick one and stay consistent.
- Seed minimal reference data in `@BeforeEach` or SQL fixtures.
- Test repository queries with realistic filter combinations (status + assignee + date range).

## Controller / API Tests

- Use `@WebMvcTest` for controller slice tests with mocked services.
- Use full `@SpringBootTest` + MockMvc for security and serialization checks.
- Assert HTTP status, response body schema, and Problem Details fields on errors.
- Every endpoint in `spec/api-contract.md` must have at least one positive and one negative test.

## State Machine Tests

- Test every allowed transition in `spec/state-machine.md`.
- Test every disallowed transition returns `409 Conflict` or domain-specific error.
- Test role restrictions on transitions (e.g., only agents may move to `IN_PROGRESS`).

## Test Data

- Use factory methods (`TicketTestFactory.createOpenTicket()`) instead of copy-pasted builders.
- Avoid random data unless testing uniqueness constraints.
- Use fixed clocks (`Clock.fixed(...)`) for SLA and timestamp assertions.

## Coverage Expectations

- Minimum 80% line coverage on `service` and `domain` packages.
- 100% coverage on state transition logic.
- Coverage is a floor, not a goal—prefer meaningful assertions over branch gaming.

## Running Tests

```bash
./gradlew test                         # Unit and integration tests
./gradlew check                        # Test plus verification tasks
./gradlew test --tests TicketServiceTest   # Single class
```

## CI Requirements

- All tests must pass on every PR.
- Flaky tests are treated as production bugs—fix or quarantine immediately.
- Integration tests must complete within 5 minutes total.

## What Not to Test

- Framework behavior (Spring wiring sanity checks beyond smoke tests).
- Generated boilerplate (Lombok, MapStruct generated code).
- Third-party library internals.
