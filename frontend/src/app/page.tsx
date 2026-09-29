"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { ErrorBanner } from "@/components/ErrorBanner";
import { PriorityBadge, StatusBadge } from "@/components/StatusBadge";
import { listTickets } from "@/lib/tickets";
import type { Ticket, TicketStatus } from "@/types/api";

const STATUSES: Array<TicketStatus | ""> = [
  "",
  "OPEN",
  "IN_PROGRESS",
  "RESOLVED",
  "CLOSED",
  "CANCELLED",
];

export default function HomePage() {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [status, setStatus] = useState("");
  const [keyword, setKeyword] = useState("");
  const [appliedKeyword, setAppliedKeyword] = useState("");
  const [error, setError] = useState<unknown>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    listTickets(status, appliedKeyword)
      .then((result) => {
        if (!cancelled) {
          setTickets(result.data);
          setError(null);
        }
      })
      .catch((err: unknown) => {
        if (!cancelled) setError(err);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [status, appliedKeyword]);

  return (
    <main className="mx-auto flex min-h-screen max-w-5xl flex-col gap-6 p-6">
      <header className="flex items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Support tickets</h1>
          <p className="text-sm text-slate-600">Search, filter, and open a ticket.</p>
        </div>
        <Link
          href="/tickets/new"
          className="rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700"
        >
          New ticket
        </Link>
      </header>

      <form
        className="flex flex-col gap-3 sm:flex-row"
        onSubmit={(event) => {
          event.preventDefault();
          setAppliedKeyword(keyword.trim());
        }}
      >
        <input
          aria-label="Search tickets"
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
          placeholder="Search title or description"
          className="flex-1 rounded border border-slate-300 px-3 py-2"
        />
        <select
          aria-label="Filter by status"
          value={status}
          onChange={(event) => setStatus(event.target.value)}
          className="rounded border border-slate-300 px-3 py-2"
        >
          {STATUSES.map((value) => (
            <option key={value || "all"} value={value}>
              {value ? value.replaceAll("_", " ") : "All statuses"}
            </option>
          ))}
        </select>
        <button
          type="submit"
          className="rounded border border-slate-300 px-4 py-2 hover:bg-slate-50"
        >
          Search
        </button>
      </form>

      <ErrorBanner error={error} />

      {loading ? (
        <p className="text-slate-600">Loading tickets…</p>
      ) : tickets.length === 0 ? (
        <p className="rounded border border-dashed border-slate-300 p-6 text-slate-600">
          No tickets match this search.
        </p>
      ) : (
        <ul className="divide-y divide-slate-200 rounded border border-slate-200">
          {tickets.map((ticket) => (
            <li key={ticket.id}>
              <Link href={`/tickets/${ticket.id}`} className="flex flex-col gap-2 p-4 hover:bg-slate-50">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="font-medium">{ticket.title}</span>
                  <StatusBadge status={ticket.status} />
                  <PriorityBadge priority={ticket.priority} />
                </div>
                <p className="line-clamp-2 text-sm text-slate-600">{ticket.description}</p>
                <p className="text-xs text-slate-500">
                  Assignee: {ticket.assignee ?? "Unassigned"}
                </p>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}
