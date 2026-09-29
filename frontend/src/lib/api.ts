import type { FieldViolation } from "@/types/api";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "";

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    public readonly title: string,
    public readonly detail: string,
    public readonly violations: FieldViolation[] = [],
    public readonly correlationId?: string,
  ) {
    super(detail || title);
    this.name = "ApiError";
  }
}

async function parseApiError(response: Response): Promise<ApiError> {
  try {
    const body = await response.json();
    const violations = Array.isArray(body.violations) ? body.violations : [];
    return new ApiError(
      response.status,
      body.title ?? "Request failed",
      body.detail ?? response.statusText,
      violations,
      body.correlationId,
    );
  } catch {
    return new ApiError(response.status, "Request failed", response.statusText);
  }
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  if (!API_BASE) {
    throw new ApiError(
      0,
      "Configuration error",
      "NEXT_PUBLIC_API_BASE_URL is not set. Copy .env.example to .env.local.",
    );
  }

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  let response: Response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...options,
      headers,
      cache: "no-store",
    });
  } catch {
    throw new ApiError(
      0,
      "Network error",
      "Could not reach the server. Check that the backend is running.",
    );
  }

  if (!response.ok) {
    throw await parseApiError(response);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}
