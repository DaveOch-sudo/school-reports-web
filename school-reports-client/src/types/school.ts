export interface School {
  id: number;
  name: string;
  code?: string;
  address?: string;
  email?: string;
  phone?: string;
  logoUrl?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateSchoolRequest {
  name: string;
  code?: string;
  address?: string;
  email?: string;
  phone?: string;
  logoUrl?: string;
}

export interface AcademicYear {
  id: number;
  label: string; // e.g. "2025" or "2025/2026"
  schoolId: number;
  startDate?: string;
  endDate?: string;
  current?: boolean;
}

export interface CreateAcademicYearRequest {
  label: string;
  schoolId: number;
  startDate?: string;
  endDate?: string;
  current?: boolean;
}
