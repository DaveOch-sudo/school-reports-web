import type { HTMLAttributes } from "react";

type BadgeVariant = "default" | "success" | "warning" | "danger" | "info";

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant;
}

const variantClasses: Record<BadgeVariant, string> = {
  default: "bg-gray-100 text-gray-700 ring-gray-200",
  success: "bg-green-50 text-green-700 ring-green-200",
  warning: "bg-amber-50 text-amber-700 ring-amber-200",
  danger: "bg-red-50 text-red-700 ring-red-200",
  info: "bg-blue-50 text-blue-700 ring-blue-200",
};

export function Badge({
  variant = "default",
  className = "",
  children,
  ...props
}: BadgeProps) {
  return (
    <span
      className={`
            inline-flex items-center rounded-full px-2.5 py-1
            text-xs font-medium ring-1 ring-inset
            ${variantClasses[variant]}
            ${className}
            `}
      {...props}
    >
      {children}
    </span>
  );
}
