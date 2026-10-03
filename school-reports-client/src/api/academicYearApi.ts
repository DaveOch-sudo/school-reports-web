import axiosClient from "./axiosClient";
import type { AcademicYear } from "../types/school";

export interface CreateAcademicYearRequest {
  label: string;
  schoolId: number;
  startDate?: string;
  endDate?: string;
  current?: boolean;
}

export const getAcademicYears = async (
  schoolId: number
): Promise<AcademicYear[]> => {
  const response = await axiosClient.get<AcademicYear[]>(
    `academic-years/school/${schoolId}`
  );
  return response.data;
};

export const getCurrentAcademicYear = async (
  schoolId: number
): Promise<AcademicYear> => {
  const response = await axiosClient.get<AcademicYear>(
    `/academic-years/school/${schoolId}/current`
  );

  return response.data;
};

export const createAcademicYear = async (
  data: CreateAcademicYearRequest
): Promise<AcademicYear> => {
  const response = await axiosClient.post<AcademicYear>(
    "/academic-years",
    data
  );

  return response.data;
};

export const setCurrentAcademicYear = async (
  id: number,
  schoolId: number
): Promise<AcademicYear> => {
  const response = await axiosClient.patch<AcademicYear>(
    `/academic-years/${id}/set-current`,
    null,
    {
      params: { schoolId },
    }
  );

  return response.data;
};

export const deleteAcademicYear = async (id: number): Promise<void> => {
  await axiosClient.delete(`/academic-years/${id}`);
};
