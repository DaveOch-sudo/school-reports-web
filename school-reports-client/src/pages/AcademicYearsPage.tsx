import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { CalendarDays, Plus } from "lucide-react";

import { useAuth } from "../context/AuthContext.tsx";
import {
  createAcademicYear,
  deleteAcademicYear,
  getAcademicYears,
  setCurrentAcademicYear,
} from "../api/academicYearApi.ts";

import type { CreateAcademicYearRequest } from "../api/academicYearApi.ts";

import Button from "../components/ui/Button.tsx";
import Input from "../components/ui/Input.tsx";
import Badge from "../components/ui/Badge.tsx";
import Modal from "../components/ui/Modal.tsx";
import DataTable from "../components/ui/DataTable.tsx";
import type { AcademicYear } from "../types/school.ts";

export default function AcademicYearsPage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const schoolId = user?.schoolId;

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [label, setLabel] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [formError, setFormError] = useState("");

  const yearsQuery = useQuery({
    queryKey: ["academic-years", schoolId],
    queryFn: () => getAcademicYears(schoolId!),
    enabled: schoolId !== undefined,
  });

  const refreshYears = async () => {
    await queryClient.invalidateQueries({
      queryKey: ["academic-years", schoolId],
    });
  };

  const createMutation = useMutation({
    mutationFn: (request: CreateAcademicYearRequest) =>
      createAcademicYear(request),
    onSuccess: async () => {
      await refreshYears();
      setIsModalOpen(false);
      setLabel("");
      setStartDate("");
      setEndDate("");
      setFormError("");
    },
    onError: () => {
      setFormError(
        "Could not create the academic year. Check the details and try again."
      );
    },
  });

  const setCurrentMutation = useMutation({
    mutationFn: (yearId: number) => setCurrentAcademicYear(yearId, schoolId!),
    onSuccess: refreshYears,
  });

  const deleteMutation = useMutation({
    mutationFn: (yearId: number) => deleteAcademicYear(yearId),
    onSuccess: refreshYears,
  });

  const years = yearsQuery.data ?? [];

  const currentYear = years.find((year) => year.current);

  const handleCreate = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setFormError("");

    if (schoolId === undefined) {
      setFormError("Your account is not associated with a school.");
      return;
    }

    if (startDate && endDate && startDate > endDate) {
      setFormError("The end date cannot be before the start date.");
      return;
    }

    createMutation.mutate({
      label: label.trim(),
      schoolId,
      ...(startDate ? { startDate } : {}),
      ...(endDate ? { endDate } : {}),
    });
  };

  const columns = [
    {
      key: "label",
      header: "Academic year",
      render: (year: AcademicYear) => (
        <span className="font-semibold text-slate-800">{year.label}</span>
      ),
    },
    {
      key: "startDate",
      header: "Start date",
      render: (year: AcademicYear) => year.startDate || "—",
    },
    {
      key: "endDate",
      header: "End date",
      render: (year: AcademicYear) => year.endDate || "—",
    },
    {
      key: "current",
      header: "Status",
      render: (year: AcademicYear) =>
        year.current ? (
          <Badge variant="success">Current</Badge>
        ) : (
          <Badge variant="default">Previous</Badge>
        ),
    },
    {
      key: "actions",
      header: "Actions",
      render: (year: AcademicYear) => (
        <div className="flex items-center gap-2">
          {!year.current && (
            <Button
              variant="secondary"
              disabled={setCurrentMutation.isPending}
              onClick={() => setCurrentMutation.mutate(year.id)}
            >
              Set current
            </Button>
          )}
          <Button
            variant="danger"
            disabled={deleteMutation.isPending}
            onClick={() => {
              if (window.confirm(`Delete academic year "${year.label}"?`)) {
                deleteMutation.mutate(year.id);
              }
            }}
          >
            Delete
          </Button>
        </div>
      ),
    },
  ];

  if (schoolId === undefined) {
    return (
      <div className="p-8">
        <h1 className="text-2xl font-bold text-slate-900">Academic Years</h1>
        <p className="mt-3 text-sm text-amber-700">
          Your account is not associated with a school. Check your user profile
          or school assignment.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-6 p-6 lg:p-8">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <div className="flex items-center gap-2 text-sm text-slate-500">
            <CalendarDays size={17} />
            <span>Academic setup</span>
          </div>
          <h1 className="mt-2 text-2xl font-bold tracking-tight text-slate-900">
            Academic Years
          </h1>
          <p className="mt-1 text-sm text-slate-500">
            Manage academic years for {user?.schoolName ?? "your school"}.
          </p>
        </div>

        <Button onClick={() => setIsModalOpen(true)}>
          <span className="inline-flex items-center gap-2">
            <Plus size={17} />
            Add academic year
          </span>
        </Button>
      </div>

      <section className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
        <p className="text-sm font-medium text-slate-500">
          Current academic year
        </p>
        {yearsQuery.isLoading ? (
          <p className="mt-2 text-sm text-slate-500">
            Loading academic years...
          </p>
        ) : currentYear ? (
          <div className="mt-3 flex flex-wrap items-center gap-3">
            <span className="text-2xl font-bold text-slate-900">
              {currentYear.label}
            </span>
            <Badge variant="success">Active</Badge>
          </div>
        ) : (
          <p className="mt-2 text-sm text-slate-500">
            No current academic year has been selected.
          </p>
        )}
      </section>

      {yearsQuery.isError && (
        <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          Could not load academic years. Check your backend connection and try
          again.
          <button
            className="ml-2 font-semibold underline"
            onClick={() => yearsQuery.refetch()}
          >
            Retry
          </button>
        </div>
      )}

      {setCurrentMutation.isError && (
        <p className="text-sm text-red-600">
          Could not change the current academic year.
        </p>
      )}

      {deleteMutation.isError && (
        <p className="text-sm text-red-600">
          Could not delete the academic year. It may still be in use.
        </p>
      )}

      <section className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        <div className="border-b border-slate-200 px-5 py-4">
          <h2 className="font-semibold text-slate-900">All academic years</h2>
          <p className="mt-1 text-sm text-slate-500">
            {years.length} academic year{years.length === 1 ? "" : "s"}{" "}
            configured
          </p>
        </div>

        {yearsQuery.isLoading ? (
          <div className="p-8 text-center text-sm text-slate-500">
            Loading academic years...
          </div>
        ) : (
          <DataTable
            columns={columns}
            data={years}
            getRowKey={(year) => year.id}
            emptyMessage="No academic years yet. Add one to get started."
          />
        )}
      </section>

      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          if (!createMutation.isPending) {
            setIsModalOpen(false);
            setFormError("");
          }
        }}
        title="Add academic year"
      >
        <form onSubmit={handleCreate} className="space-y-4">
          <Input
            label="Academic year"
            placeholder="e.g. 2026 or 2026/2027"
            value={label}
            onChange={(event) => setLabel(event.target.value)}
            required
          />

          <Input
            label="Start date (optional)"
            type="date"
            value={startDate}
            onChange={(event) => setStartDate(event.target.value)}
          />

          <Input
            label="End date (optional)"
            type="date"
            value={endDate}
            onChange={(event) => setEndDate(event.target.value)}
          />

          {formError && <p className="text-sm text-red-600">{formError}</p>}

          <div className="flex justify-end gap-3 pt-2">
            <Button
              type="button"
              variant="secondary"
              disabled={createMutation.isPending}
              onClick={() => setIsModalOpen(false)}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              loading={createMutation.isPending}
              disabled={!label.trim()}
            >
              Create year
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
