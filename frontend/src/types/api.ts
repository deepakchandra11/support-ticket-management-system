export type TicketStatus =
  | "OPEN"
  | "IN_PROGRESS"
  | "RESOLVED"
  | "CLOSED"
  | "CANCELLED";

export type TicketPriority = "LOW" | "MEDIUM" | "HIGH" | "URGENT";

export interface FieldViolation {
  field: string;
  message: string;
}

export interface Comment {
  id: string;
  body: string;
  author: string;
  createdAt: string;
}

export interface Ticket {
  id: string;
  title: string;
  description: string;
  priority: TicketPriority;
  status: TicketStatus;
  assignee: string | null;
  createdAt: string;
  updatedAt: string;
  allowedTransitions: TicketStatus[];
  comments: Comment[];
}

export interface TicketList {
  data: Ticket[];
}
