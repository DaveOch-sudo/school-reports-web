import { LogOut, UserCircle } from "lucide-react";
import { useAuth } from "../../context/AuthContext.tsx";

export default function Header() {
  const { user, logout } = useAuth();

  return (
    <header className="fixed top-0 right-0 left-64 z-30 h-16 border-b border-slate-200 bg-white/95 shadow-sm backdrop-blur">
      <div className="flex h-full items-center justify-between px-6">
        {/* Page context */}
        <div>
          <p className="text-xs font-medium uppercase tracking-wide text-slate-400">
            School Reports
          </p>

          <p className="text-sm font-medium text-slate-700">
            Academic workspace
          </p>
        </div>

        {/* User area */}
        <div className="flex items-center gap-4">
          <div className="hidden text-right sm:block">
            <p className="text-sm font-semibold text-slate-800">
              {user?.name ?? "User"}
            </p>

            <p className="text-xs text-slate-500">{user?.role ?? ""}</p>
          </div>

          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-slate-100 text-slate-600">
            <UserCircle size={22} />
          </div>

          <div className="h-7 w-px bg-slate-200" />

          <button
            type="button"
            onClick={logout}
            className="inline-flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium text-slate-500 transition hover:bg-red-50 hover:text-red-600 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
          >
            <LogOut size={17} />
            <span className="hidden sm:inline">Logout</span>
          </button>
        </div>
      </div>
    </header>
  );
}
