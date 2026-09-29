"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { FormEvent, useEffect, useState } from "react";
import { ErrorBanner } from "@/components/ErrorBanner";
import { StatusBadge, statusLabel } from "@/components/StatusBadge";
import { addComment, getTicket, transitionTicket, updateTicket } from "@/lib/tickets";
import type { Ticket, TicketPriority, TicketStatus } from "@/types/api";

export default function TicketDetailPage() {
  const params = useParams<{ id: string }>();
  const ticketId = params.id;
  const [ticket, setTicket] = useState<Ticket | null>(null);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState<TicketPriority>("MEDIUM");
  const [assignee, setAssignee] = useState("");
  const [comment, setComment] = useState("");
  const [author, setAuthor] = useState("Support user");
  const [error, setError] = useState<unknown>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const stored = window.localStorage.getItem("ticket-author");
    if (stored) setAuthor(stored);
  }, []);

  useEffect(() => {
    let cancelled = false;
    getTicket(ticketId)
      .then((loaded) => {
        if (cancelled) return;
        setTicket(loaded);
        setTitle(loaded.title);
        setDescription(loaded.description);
        setPriority(loaded.priority);
        setAssignee(loaded.assignee ?? "");
        setError(null);
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
  }, [ticketId]);

  function applyTicket(next: Ticket) {
    setTicket(next);
    setTitle(next.title);
    setDescription(next.description);
    setPriority(next.priority);
    setAssignee(next.assignee ?? "");
  }

  async function onSave(event: FormEvent) {
    event.preventDefault();
    setError(null);
    try {
      const updated = await updateTicket(ticketId, {
        title,
        description,
        priority,
        assignee,
      });
      applyTicket(updated);
    } catch (err) {
      setError(err);
    }
  }

  async function onTransition(targetStatus: TicketStatus) {
    setError(null);
    try {
      const updated = await transitionTicket(ticketId, targetStatus);
      applyTicket(updated);
    } catch (err) {
      setError(err);
    }
  }

  async function onComment(event: FormEvent) {
    event.preventDefault();
    setError(null);
    try {
      window.localStorage.setItem("ticket-author", author);
      await addComment(ticketId, comment, author);
      setComment("");
      applyTicket(await getTicket(ticketId));
    } catch (err) {
      setError(err);
    }
  }

  if (loading) {
    return <main className="p-6 text-slate-600">Loading ticket…</main>;
  }

  if (!ticket) {
    return (
      <main className="mx-auto flex max-w-2xl flex-col gap-4 p-6">
        <Link href="/" className="text-sm text-slate-600 hover:underline">
          Back to tickets
        </Link>
        <ErrorBanner error={error} />
      </main>
    );
  }

  return (
    <main className="mx-auto flex min-h-screen max-w-3xl flex-col gap-6 p-6">
      <Link href="/" className="text-sm text-slate-600 hover:underline">
        Back to tickets
      </Link>
      <div className="flex flex-wrap items-center gap-3">
        <h1 className="text-2xl font-bold">Ticket</h1>
        <StatusBadge status={ticket.status} />
      </div>
      <ErrorBanner error={error} />

      <form onSubmit={onSave} className="flex flex-col gap-4 rounded border border-slate-200 p-4">
        <label className="flex flex-col gap-1 text-sm">
          Title
          <input
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            className="rounded border border-slate-300 px-3 py-2"
          />
        </label>
        <label className="flex flex-col gap-1 text-sm">
          Description
          <textarea
            rows={5}
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            className="rounded border border-slate-300 px-3 py-2"
          />
        </label>
        <label className="flex flex-col gap-1 text-sm">
          Priority
          <select
            value={priority}
            onChange={(event) => setPriority(event.target.value as TicketPriority)}
            className="rounded border border-slate-300 px-3 py-2"
          >
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="URGENT">URGENT</option>
          </select>
        </label>
        <label className="flex flex-col gap-1 text-sm">
          Assignee
          <input
            value={assignee}
            onChange={(event) => setAssignee(event.target.value)}
            placeholder="Unassigned"
            className="rounded border border-slate-300 px-3 py-2"
          />
        </label>
        <button type="submit" className="w-fit rounded bg-slate-900 px-4 py-2 text-white hover:bg-slate-700">
          Save changes
        </button>
      </form>

      <section className="flex flex-col gap-3">
        <h2 className="text-lg font-semibold">Status</h2>
        {ticket.allowedTransitions.length === 0 ? (
          <p className="text-sm text-slate-600">No further status changes are allowed.</p>
        ) : (
          <div className="flex flex-wrap gap-2">
            {ticket.allowedTransitions.map((target) => (
              <button
                key={target}
                type="button"
                onClick={() => onTransition(target)}
                className="rounded border border-slate-300 px-3 py-2 text-sm hover:bg-slate-50"
              >
                Move to {statusLabel(target)}
              </button>
            ))}
          </div>
        )}
      </section>

      <section className="flex flex-col gap-4">
        <h2 className="text-lg font-semibold">Comments</h2>
        {ticket.comments.length === 0 ? (
          <p className="text-sm text-slate-600">No comments yet.</p>
        ) : (
          <ul className="flex flex-col gap-3">
            {ticket.comments.map((item) => (
              <li key={item.id} className="rounded border border-slate-200 p-3">
                <p className="text-sm">{item.body}</p>
                <p className="mt-2 text-xs text-slate-500">
                  {item.author} · {new Date(item.createdAt).toLocaleString()}
                </p>
              </li>
            ))}
          </ul>
        )}
        <form onSubmit={onComment} className="flex flex-col gap-3">
          <label className="flex flex-col gap-1 text-sm">
            Your name
            <input
              value={author}
              onChange={(event) => setAuthor(event.target.value)}
              className="rounded border border-slate-300 px-3 py-2"
            />
          </label>
          <label className="flex flex-col gap-1 text-sm">
            Comment
            <textarea
              rows={3}
              value={comment}
              onChange={(event) => setComment(event.target.value)}
              className="rounded border border-slate-300 px-3 py-2"
            />
          </label>
          <button type="submit" className="w-fit rounded border border-slate-300 px-4 py-2 hover:bg-slate-50">
            Add comment
          </button>
        </form>
      </section>
    </main>
  );
}
