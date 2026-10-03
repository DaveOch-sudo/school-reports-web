import type { InputHTMLAttributes } from "react";

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export default function Input({
  label,
  error,
  id,
  className = "",
  ...props
}: InputProps) {
  return (
    <div className="flex flex-col gap-1.5">
      {label && (
        <label htmlFor={id} className="text-sm font-medium text-gray-700">
          {label}
        </label>
      )}
      <input
        id={id}
        className={`rounded-md border border-gray-300 px-3 py-2 text-sm
                  outline-none transition
                  focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20
                  disabled:cursor-not-allowed disabled:bg-gray-100
                  ${error ? "border-red-500" : ""}
                  ${className}`}
        {...props}
      />

      {error && <span className="text-xs text-red-600">{error}</span>}
    </div>
  );
}
