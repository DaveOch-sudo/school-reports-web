import type {ButtonHTMLAttributes, ReactNode} from "react";

type ButtonVariant = "primary" | "secondary" | "danger";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement>{
    variant?: ButtonVariant;
    loading?: boolean;
    children: ReactNode;
}

export function Button({
    variant = "primary",
    loading = false,
    disabled,
    children,
    ...props
}: ButtonProps) {
    const variantClasses = {
        primary: "bg-blue-600 text-white hover:bg-blue-700",
        secondary: "bg-gray-100 text-gray-900 hover:bg-gray-200",
        danger: "bg-red-600 text-white hover:bg-red-700",
    };

    return (
        <button
            {...props}
            disabled={disabled || loading}
            className={`${variantClasses[variant]} ${
                disabled || loading
                ? "cursor-not-allowed opacity-60" 
                    : ""
            } rounded-md px-4 py-2 text-sm font-medium transition-colors`}>
            {loading ? "Loading..." : children}
        </button>
    );
}