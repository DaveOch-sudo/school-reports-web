import type {SelectHTMLAttributes} from "react";


interface SelectOption {
    value: string;
    label: string;
}

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement>{
    label?: string;
    error?: string;
    options: SelectOption[];
    placeholder?: string;
}

export function Select({
    label,
    error,
    options,
    placeholder,
    id,
    className = "",
    ...props
}: SelectProps) {
    return (
        <div className="flex flex-col gap-1.5">
            {label && (
                <label
                    htmlFor={id}
                    className="text-sm font-medium text-gray-700"
                    >
                    {label}
                </label>
            )}
            <select
                id={id}
                className={`
                  w-full rounded-md border border-gray-300
                  bg-white px-3 py-2 text-sm text-gray-900
                  outline-none transition
                  focus:border-blue-500
                  focus:ring-2 focus:ring-blue-500/20
                  disabled:cursor-not-allowed
                  disabled:bg-gray-100
                  disabled:text-gray-500
                  ${error ? "border-red-500" : ""}
                  ${className}
                `}
                {...props}>
                {placeholder && (
                    <option value="" disabled>
                        {placeholder}
                    </option>
                )}

                {options.map((option) => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
            {error && (
                <span className="text-xs text-red-600">
                    {error}
                </span>
            )}
        </div>
    )
}