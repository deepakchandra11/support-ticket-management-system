---
description: Generate test cases and test code stubs from specifications and existing implementation.
---

# Generate Tests

Generate tests for the Support Ticket Management System based on specifications and (optionally) existing code.

## Inputs

- Target: specific class, endpoint, feature, or user story
- `spec/test-strategy.md` (primary guide)
- `spec/requirements.md`, `spec/api-contract.md`, `spec/state-machine.md`, `spec/ui-flow.md`
- `rules/testing.md`
- Existing source and test files (if implementation exists)

## Process

1. **Identify test scope**
   - Map the target to requirement IDs and API endpoints.
   - Determine test layer: unit, integration, contract, or e2e per `rules/testing.md`.

2. **Derive test cases**
   - For each requirement: at least one happy-path and one negative test.
   - For each API endpoint: auth, validation, not-found, and conflict scenarios.
   - For each state transition in `spec/state-machine.md`: allowed and disallowed cases.
   - For each UI flow step in `spec/ui-flow.md`: corresponding API or e2e test.

3. **Name tests**
   - Follow convention: `should{ExpectedBehavior}_when{Condition}`.
   - Prefix with requirement or endpoint reference in a comment: `// REQ-003, POST /tickets`.

4. **Generate test code**
   - Use JUnit 5, Mockito, AssertJ, and Testcontainers per project standards.
   - Use test factories from `src/test/java/.../fixtures/` when they exist; otherwise define minimal inline setup.
   - Include `@DisplayName` with plain-language descriptions.

5. **Output test plan first**
   - Present the test case table before generating code unless the user asks for code only.

## Test Case Table Template

| ID | Layer | Requirement | Scenario | Given | When | Then |
|----|-------|-------------|----------|-------|------|------|
| TC-001 | Unit | REQ-003 | Create open ticket | Valid input | createTicket() | Returns ticket in OPEN status |

## Code Generation Rules

- Place unit tests in `src/test/java` mirroring main package structure.
- Integration tests suffix: `IT`.
- Do not test framework internals.
- Use fixed `Clock` for time-dependent SLA tests.
- Mock external services; use real database via Testcontainers for repository tests.
- Assert Problem Details structure for error responses.

## Output Format

```markdown
# Test Plan: [feature/endpoint]

## Scope
[What is being tested]

## Test Cases
[Table]

## Generated Files
- `path/to/TestClass.java` — [description]

## Coverage Notes
[What is covered, intentional gaps, follow-up tests needed]
```

Then provide the generated test class(es) in fenced code blocks.

## Rules

- Every generated test must trace to a spec reference.
- Do not generate trivial tests (getters/setters, empty constructors).
- Prefer parameterized tests for state machine transition tables.
- If implementation does not exist yet, generate test skeletons with `// TODO: implement when [class] exists` only when unavoidable; prefer fully compilable tests against interfaces or expected contracts.
