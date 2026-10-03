import {
  Users,
  School,
  ClipboardCheck,
  FileText,
  CalendarDays,
  ArrowRight,
  BookOpen,
} from "lucide-react";

interface DashboardStat {
  label: string;
  icon: typeof Users;
  description: string;
  iconClass: string;
}

const dashboardStats: DashboardStat[] = [
  {
    label: "Total Students",
    icon: Users,
    description: "Registered students",
    iconClass: "bg-blue-50 text-blue-600",
  },
  {
    label: "Classes",
    icon: School,
    description: "Organised class groups",
    iconClass: "bg-emerald-50 text-emerald-600",
  },
  {
    label: "Marksheets Graded",
    icon: ClipboardCheck,
    description: "Completed subject marksheets",
    iconClass: "bg-violet-50 text-violet-600",
  },
  {
    label: "Reports Published",
    icon: FileText,
    description: "Published student reports",
    iconClass: "bg-amber-50 text-amber-600",
  },
];

export default function DashboardPage() {
  return (
    <div className="space-y-8 p-4 sm:p-6 lg:p-8">
      {/*Page heading */}
      <section className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="mt-1 text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
            Dashboard
          </h1>

          <p className="mt-2 text-sm text-gray-500">
            An overview of your school&apos;s reporting activities.
          </p>
        </div>

        <div className="flex items-center gap-2 self-start rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm text-gray-600">
          <CalendarDays size={18} />
          <span>Academic overview</span>
        </div>
      </section>
      {/* Summary cards */}
      <section>
        <div className="mb-4">
          <h2 className="text-base font-semibold text-gray-900">
            School at a glance
          </h2>

          <p className="mt-1 text-sm text-gray-500">
            Key figures will appear here when connected to your school data.
          </p>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {dashboardStats.map((stat) => {
            const Icon = stat.icon;

            return (
              <article
                key={stat.label}
                className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm"
              >
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="text-sm font-medium text-gray-500">
                      {stat.label}
                    </p>

                    <p className="mt-3 text-3xl font-semibold text-gray-900">
                      —
                    </p>
                  </div>

                  <div className={`rounded-lg p-3 ${stat.iconClass}`}>
                    <Icon size={21} />
                  </div>
                </div>

                <p className="mt-4 text-xs text-gray-500">{stat.description}</p>
              </article>
            );
          })}
        </div>
      </section>
      {/* Academic context */}
      <section className="rounded-xl border border-gray-200 bg-white p-5 sm:p-6">
        <div className="flex items-start gap-3">
          <div className="rounded-lg bg-blue-50 p-3 text-blue-600">
            <BookOpen size={22} />
          </div>

          <div>
            <h2 className="font-semibold text-gray-900">Academic overview</h2>

            <p className="mt-1 text-sm text-gray-500">
              Your school&apos;s academic year, term, and reporting progress
              will be displayed here.
            </p>
          </div>
        </div>

        <div className="mt-6 grid grid-cols-1 gap-4 border-t border-gray-100 pt-5 sm:grid-cols-2">
          <div>
            <p className="text-sm text-gray-500">Academic year</p>
            <p className="mt-1 font-medium text-gray-400">Not configured</p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Current term</p>
            <p className="mt-1 font-medium text-gray-400">Not configured</p>
          </div>
        </div>
      </section>

      {/* Workflow placeholder */}
      <section className="rounded-xl border border-dashed border-gray-300 bg-gray-50 p-6 sm:p-8">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-start gap-3">
            <div className="rounded-lg bg-white p-3 text-gray-600 shadow-sm">
              <ClipboardCheck size={22} />
            </div>

            <div>
              <h2 className="font-semibold text-gray-900">
                Reporting workflow
              </h2>

              <p className="mt-1 max-w-xl text-sm leading-6 text-gray-600">
                Manage classes and students, enter subject marks, compile
                general marksheets, and prepare reports for publication.
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={() => {
              window.location.href = "/classes";
            }}
            className="inline-flex shrink-0 items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
          >
            View classes
            <ArrowRight size={16} />
          </button>
        </div>
      </section>
    </div>
  );
}
