import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  Users,
  School,
  ClipboardList,
  FileText,
} from "lucide-react";
import { useAuth } from "../../context/AuthContext.tsx";

export default function Sidebar() {
  const menuItems = [
    {
      label: "Dashboard",
      path: "/dashboard",
      icon: LayoutDashboard,
      roles: [
        "SUPER_ADMIN",
        "SCHOOL_ADMIN",
        "CLASS_TEACHER",
        "SUBJECT_TEACHER",
      ],
    },
    {
      label: "Students",
      path: "/students",
      icon: Users,
      roles: ["SUPER_ADMIN", "SCHOOL_ADMIN"],
    },
    {
      label: "Classes",
      path: "/classes",
      icon: School,
      roles: ["SUPER_ADMIN", "SCHOOL_ADMIN"],
    },
    {
      label: "Marksheets",
      path: "/marksheets",
      icon: ClipboardList,
      roles: [
        "SUPER_ADMIN",
        "SCHOOL_ADMIN",
        "CLASS_TEACHER",
        "SUBJECT_TEACHER",
      ],
    },
    {
      label: "Reports",
      path: "/reports",
      icon: FileText,
      roles: ["SUPER_ADMIN", "SCHOOL_ADMIN", "CLASS_TEACHER"],
    },
  ];

  const { hasAnyRole } = useAuth();

  return (
    <aside className="fixed inset-y-0 left-0 z-40 flex w-64 flex-col border-r border-slate-800 bg-slate-950 text-slate-300">
      {/* Brand */}
      <div className="border-b border-slate-800 px-6 py-6">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600 text-sm font-bold text-white shadow-lg shadow-blue-950/40">
            SR
          </div>

          <div>
            <h2 className="text-base font-semibold tracking-tight text-white">
              School Reports
            </h2>

            <p className="mt-0.5 text-xs text-slate-500">Academic management</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-6">
        <p className="mb-3 px-3 text-[11px] font-semibold uppercase tracking-wider text-slate-600">
          Main menu
        </p>

        <ul className="space-y-1">
          {menuItems
            .filter((item) => hasAnyRole(...item.roles))
            .map((item) => {
              const Icon = item.icon;

              return (
                <li key={item.path}>
                  <NavLink
                    to={item.path}
                    className={({ isActive }) =>
                      [
                        "group flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all",
                        isActive
                          ? "bg-blue-600 text-white shadow-md shadow-blue-950/30"
                          : "text-slate-400 hover:bg-slate-900 hover:text-slate-100",
                      ].join(" ")
                    }
                  >
                    <Icon size={18} strokeWidth={1.8} className="shrink-0" />

                    <span>{item.label}</span>
                  </NavLink>
                </li>
              );
            })}
        </ul>
      </nav>

      {/* Bottom section */}
      <div className="border-t border-slate-800 px-4 py-4">
        <p className="px-2 text-xs leading-5 text-slate-600">
          School Reports
          <br />
          Academic reporting system
        </p>
      </div>
    </aside>
  );
}
