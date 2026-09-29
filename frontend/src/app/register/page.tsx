import Link from "next/link";

export default function RegisterPage() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-center gap-4 p-8">
      <h1 className="text-2xl font-bold">Register</h1>
      <p className="text-gray-600 dark:text-gray-400">
        Registration form placeholder — implement per spec/api-contract.md POST /auth/register
      </p>
      <Link href="/" className="text-blue-600 hover:underline">
        Back to home
      </Link>
    </main>
  );
}
