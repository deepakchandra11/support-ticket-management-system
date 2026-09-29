import { apiFetch } from "@/lib/api";
import type {
  Comment,
  Ticket,
  TicketList,
  TicketPriority,
  TicketStatus,
} from "@/types/api";

export interface TicketInput {
  title: string;
  description: string;
  priority: TicketPriority;
  assignee?: string;
}

export function listTickets(status?: string, keyword?: string): Promise<TicketList> {
  const params = new URLSearchParams();
  if (status) params.set("status", status);
  if (keyword) params.set("q", keyword);
  const query = params.toString();
  return apiFetch<TicketList>(`/tickets${query ? `?${query}` : ""}`);
}

export function getTicket(id: string): Promise<Ticket> {
  return apiFetch<Ticket>(`/tickets/${id}`);
}

export function createTicket(input: TicketInput): Promise<Ticket> {
  return apiFetch<Ticket>("/tickets", {
    method: "POST",
    body: JSON.stringify(input),
  });
}

export function updateTicket(id: string, input: TicketInput): Promise<Ticket> {
  return apiFetch<Ticket>(`/tickets/${id}`, {
    method: "PUT",
    body: JSON.stringify(input),
  });
}

export function transitionTicket(id: string, targetStatus: TicketStatus): Promise<Ticket> {
  return apiFetch<Ticket>(`/tickets/${id}/transitions`, {
    method: "POST",
    body: JSON.stringify({ targetStatus }),
  });
}

export function addComment(id: string, body: string, author: string): Promise<Comment> {
  return apiFetch<Comment>(`/tickets/${id}/comments`, {
    method: "POST",
    body: JSON.stringify({ body, author }),
  });
}
