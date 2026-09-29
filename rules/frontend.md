# Frontend Guidelines

Project conventions for the Support Ticket Management System web application.

**Location**: All frontend code lives in [`frontend/`](../frontend/) at the repository root. API contract is defined in [`spec/api-contract.md`](../spec/api-contract.md).

## Stack

- Next.js (App Router)
- React 19
- TypeScript (strict mode)
- CSS Modules or Tailwind CSS (match existing patterns in `frontend/`)

## Directory Structure

```
frontend/
├── src/
│   ├── app/              # Routes, layouts, page components
│   ├── components/       # Reusable UI (StatusBadge, CommentThread, etc.)
│   ├── lib/              # API client, auth, utilities
│   └── types/            # Types aligned with api-contract.md
├── public/
├── .env.example
└── package.json
```

## Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| `NEXT_PUBLIC_API_BASE_URL` | Yes | Backend base URL (e.g. `http://localhost:8080/api/v1`) |

- Prefix client-exposed vars with `NEXT_PUBLIC_`.
- Never commit `.env.local`; commit `.env.example` with placeholder values.
- Server-only secrets (if any) use unprefixed names and are read only in Server Components or Route Handlers.

## API Client

- Centralize HTTP calls in `src/lib/api.ts` (or `src/lib/api/` by resource).
- Read base URL from `process.env.NEXT_PUBLIC_API_BASE_URL`.
- Attach JWT via `Authorization: Bearer <token>` on authenticated requests.
- Parse RFC 7807 Problem Details on errors; surface `detail` and `correlationId` to users when appropriate.
- Do not hardcode API URLs in components.

Example pattern:

```typescript
const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL;

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = getAccessToken(); // from auth context or secure storage
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });
  if (!res.ok) throw await parseApiError(res);
  return res.json() as Promise<T>;
}
```

## Routing and Layouts

- Follow routes defined in [`spec/ui-flow.md`](../spec/ui-flow.md).
- Use route groups for role-based areas: `(customer)`, `(agent)`, `(admin)`.
- Protect routes with middleware or layout-level auth checks.
- Redirect unauthenticated users to `/login`; redirect by role after login.

## Components

- Prefer Server Components for data-fetching pages where SSR adds value.
- Use Client Components (`'use client'`) for interactivity: forms, modals, live updates.
- Keep components focused; extract hooks for shared logic (`useAuth`, `useTickets`).
- Map UI status/priority labels to enums in `spec/data-model.md` — do not invent new values.

## State and Auth

- JWT access token: store in memory or `sessionStorage` for v1 (see security trade-offs in implementation).
- Refresh token: prefer httpOnly cookie set by backend if/when refresh flow is implemented.
- Use React context or a lightweight store for auth state; avoid prop drilling user/role.
- Clear tokens on logout and on 401 responses.

## Styling and Accessibility

- Meet WCAG 2.1 AA per NFR-007 in requirements.
- Status badges use color plus icon/text (not color alone).
- All form fields have associated `<label>` elements.
- Announce dynamic updates (status changes) via ARIA live regions.

## Testing

- Unit tests: Vitest or Jest for utilities and hooks.
- Component tests: React Testing Library.
- E2E smoke tests: Playwright for critical flows in `spec/ui-flow.md`.
- Mock API in unit tests; use staging or MSW for integration-style component tests.

## Code Quality

- Run `npm run lint` and `npm run build` before opening a PR.
- Enable TypeScript strict mode; no `any` without justification.
- Align request/response types with `spec/api-contract.md`; update types when the contract changes.

## What Not to Do

- Do not call the database directly from the frontend.
- Do not duplicate business rules (state transitions, role checks) — enforce on backend; UI may hide disallowed actions using `allowedTransitions` from API.
- Do not embed backend code or copy `spec/` into `frontend/`.
