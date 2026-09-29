---
description: Review specification documents for completeness, consistency, and implementability.
---

# Review Spec

Review specification documents in `spec/` for the Support Ticket Management System.

## Inputs

- One or more files from `spec/` (or the entire `spec/` directory)
- Steering rules: `rules/` directory
- User context or feature description (if provided)

## Review Process

1. **Completeness**
   - Are all user stories in `requirements.md` traceable to API, data model, UI flow, and tests?
   - Does every entity in `data-model.md` appear in `api-contract.md` where needed?
   - Does `state-machine.md` cover every status referenced in requirements and API?
   - Does `test-strategy.md` cover every critical path in `ui-flow.md`?

2. **Consistency**
   - Terminology aligned across all spec files (status names, role names, field names)?
   - API request/response shapes match `data-model.md` fields and types?
   - Architecture decisions in `architecture.md` support the requirements without contradiction?

3. **Clarity**
   - Are requirements unambiguous and testable?
   - Are acceptance criteria specific (given/when/then or measurable outcomes)?
   - Are open questions explicitly listed?

4. **Implementability**
   - Can a developer implement from these specs without guessing?
   - Are non-functional requirements quantified (latency, concurrency, retention)?
   - Are security and authorization rules defined per endpoint and per transition?

5. **Standards alignment**
   - API design follows `rules/api-standards.md`?
   - Testing approach follows `rules/testing.md`?
   - Technology choices align with `rules/java-springboot.md`?

## Cross-Reference Matrix

Build a traceability matrix:

| Requirement ID | Data Model | API Endpoint | State Transition | UI Flow | Test Case |
|----------------|------------|--------------|------------------|---------|-----------|
| REQ-001 | Ticket | POST /tickets | OPEN | Create ticket | TC-001 |

Flag any requirement with missing links.

## Output Format

```markdown
# Spec Review: [scope]

## Executive Summary
[Overall readiness: READY | NEEDS WORK | BLOCKED]

## Strengths
- [What is well-defined]

## Gaps and Issues

### Blocking
- [Issue]: [file] — [recommended resolution]

### Non-blocking
- [Issue]: [file] — [recommended resolution]

## Consistency Findings
| Area | Issue | Files Affected |
|------|-------|----------------|

## Open Questions
- [Question needing stakeholder input]

## Recommendations
1. [Prioritized action items]

## Verdict
READY FOR IMPLEMENTATION | REVISE AND RE-REVIEW
```

## Rules

- Do not propose implementation code; suggest spec amendments only.
- Prefer specific, editable spec text over vague feedback.
- When specs conflict, cite both locations and recommend a single source of truth.
- Ensure state machine has no orphan states or unreachable transitions.
