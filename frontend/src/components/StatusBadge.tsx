import type { TicketPriority, TicketStatus } from "@/types/api";

const STATUS_STYLES: Record<TicketStatus, string> = {
  OPEN: "bg-sky-100 text-sky-800",
  IN_PROGRESS: "bg-amber-100 text-amber-900",
  RESOLVED: "bg-emerald-100 text-emerald-800",
  CLOSED: "bg-slate-200 text-slate-700",
  CANCELLED: "bg-rose-100 text-rose-800",
};

const PRIORITY_STYLES: Record<TicketPriority, string> = {
  LOW: "bg-slate-100 text-slate-700",
  MEDIUM: "bg-blue-100 text-blue-800",
  HIGH: "bg-orange-100 text-orange-800",
  URGENT: "bg-red-100 text-red-800",
};

export function statusLabel(status: TicketStatus): string {
  return status.replaceAll("_", " ");
}

export function StatusBadge({ status }: { status: TicketStatus }) {
  return (
    <span className={`rounded-full px-2 py-1 text-xs font-semibold ${STATUS_STYLES[status]}`}>
      {statusLabel(status)}
    </span>
  );
}

export function PriorityBadge({ priority }: { priority: TicketPriority }) {
  return (
    <span className={`rounded-full px-2 py-1 text-xs font-semibold ${PRIORITY_STYLES[priority]}`}>
      {priority}
    </span>
  );
}
