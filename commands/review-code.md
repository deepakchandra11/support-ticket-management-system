---
description: Review implementation code against project rules, specifications, and best practices.
---

# Review Code

Review the selected or recently changed code for the Support Ticket Management System.

## Inputs

- Changed files (git diff) or files explicitly referenced by the user
- Project rules: `rules/java-springboot.md`, `rules/testing.md`, `rules/api-standards.md`
- Specifications: `spec/` directory

## Review Process

1. **Understand the change**
   - Summarize what the code does in 2–3 sentences.
   - Identify which spec documents the change relates to.

2. **Correctness**
   - Does the logic match `spec/requirements.md` and `spec/state-machine.md`?
   - Are edge cases handled (null inputs, invalid transitions, unauthorized access)?
   - Are transactions scoped correctly?

3. **Architecture and layering**
   - Controllers thin? Business logic in services?
   - DTOs at API boundary? No entity leakage?
   - Package structure follows `rules/java-springboot.md`?

4. **API compliance**
   - Endpoints match `spec/api-contract.md` (paths, methods, status codes)?
   - Error responses use Problem Details format?
   - Pagination and filtering follow `rules/api-standards.md`?

5. **Security**
   - Authentication and authorization enforced?
   - User identity derived from security context, not request body?
   - Input validation on all external inputs?

6. **Testing**
   - Tests exist for new behavior per `rules/testing.md`?
   - State transitions covered?
   - Negative paths tested?

7. **Maintainability**
   - Clear naming, reasonable method length?
   - No unnecessary dependencies?
   - Migrations included for schema changes?

## Output Format

```markdown
# Code Review: [brief title]

## Summary
[What changed and overall assessment]

## Findings

### Critical (must fix)
- [file:line] Description and suggested fix

### Suggestions (should consider)
- [file:line] Description and suggested fix

### Nice to Have
- [file:line] Description

## Spec Alignment
| Spec | Status | Notes |
|------|--------|-------|
| requirements.md | ✅/⚠️/❌ | |
| api-contract.md | ✅/⚠️/❌ | |
| state-machine.md | ✅/⚠️/❌ | |

## Test Coverage Assessment
[What is covered, what is missing]

## Verdict
APPROVE | APPROVE WITH SUGGESTIONS | REQUEST CHANGES
```

## Rules

- Cite specific file paths and line numbers when possible.
- Do not suggest implementation rewrites unless the current approach violates specs or rules.
- Flag any drift between code and `spec/` explicitly.
- Do not approve changes that lack tests for new business logic.
