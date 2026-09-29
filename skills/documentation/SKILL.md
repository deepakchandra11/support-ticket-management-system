---
name: documentation
description: >-
  Create and maintain project documentation for the Support Ticket Management
  System. Use when writing README files, API docs, architecture decision records,
  runbooks, onboarding guides, or updating docs to match spec/ and implementation.
---

# Documentation

## When to Use

Apply this skill when the user asks to:

- Write or update README, CONTRIBUTING, or onboarding docs
- Document APIs, architecture, or deployment procedures
- Create ADRs (Architecture Decision Records)
- Sync documentation with changes in `spec/` or the codebase

## Documentation Hierarchy

| Document | Location | Audience | Update Trigger |
|----------|----------|----------|----------------|
| Specifications | `spec/` | Engineers, PM, QA | Feature design changes |
| API contract | `spec/api-contract.md` | Frontend, integrators | Endpoint changes |
| OpenAPI | `docs/openapi.yaml` | Tooling, clients | API implementation |
| ADRs | `docs/adr/` | Engineers | Architectural decisions |
| Runbooks | `docs/runbooks/` | Ops, on-call | Infra or incident learnings |
| README | `README.md` | New contributors | Setup or scope changes |

## Writing Standards

1. **Lead with purpose** — first sentence states what the document covers and who it is for.
2. **Stay synchronized** — if code diverges from `spec/`, update the spec or flag the drift explicitly.
3. **Use examples** — include request/response samples for APIs; include diagrams for flows.
4. **Be actionable** — prefer commands and checklists over prose.
5. **Avoid duplication** — link to `spec/` rather than copying requirements into README.

## API Documentation Template

For each endpoint, document:

```markdown
### POST /api/v1/tickets

**Auth**: CUSTOMER, AGENT, ADMIN

**Request**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | string | yes | Short summary (max 200 chars) |

**Response** `201 Created`
[JSON example]

**Errors**
| Status | Type | When |
|--------|------|------|
| 400 | validation-error | Missing title |
```

## ADR Template

Store as `docs/adr/NNNN-short-title.md`:

```markdown
# NNNN. Title

## Status
Proposed | Accepted | Deprecated

## Context
What problem are we solving?

## Decision
What did we decide?

## Consequences
Positive and negative outcomes.
```

## Diagrams

- Use Mermaid for sequence diagrams, state machines, and component views.
- Keep diagrams in the doc that references them; do not scatter across files.
- For state machines, mirror `spec/state-machine.md`.

## Review Checklist

- [ ] Terminology matches `spec/` (ticket, agent, customer, status names)
- [ ] All code snippets are tested or copied from working examples
- [ ] Links resolve within the repository
- [ ] No secrets, tokens, or real credentials in examples
- [ ] Version numbers and paths reflect the current project layout

## Additional Resources

- Requirements: [spec/requirements.md](../../spec/requirements.md)
- Architecture: [spec/architecture.md](../../spec/architecture.md)
- API contract: [spec/api-contract.md](../../spec/api-contract.md)
