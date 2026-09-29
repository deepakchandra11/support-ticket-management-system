import { ApiError } from "@/lib/api";

export function ErrorBanner({ error }: { error: unknown }) {
  if (!error) return null;

  const title = error instanceof ApiError ? error.title : "Something went wrong";
  const detail = error instanceof Error ? error.message : "Unexpected error";
  const violations = error instanceof ApiError ? error.violations : [];

  return (
    <div
      role="alert"
      className="rounded-md border border-red-300 bg-red-50 px-4 py-3 text-sm text-red-800"
    >
      <p className="font-semibold">{title}</p>
      <p className="mt-1">{detail}</p>
      {violations.length > 0 && (
        <ul className="mt-2 list-disc pl-5">
          {violations.map((violation) => (
            <li key={`${violation.field}-${violation.message}`}>
              {violation.message}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
