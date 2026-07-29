import type {Gender} from "./enums.ts";

export interface SchoolClass {
    id: number;
    className: string;
    code?: string;
}

export interface Student {
    id: number;
    admissionNumber: number;
    name: string;
    schoolClassId: number;
    schoolClassName: string;
    lin: string;
    dob: string;
    age: number;
    gender: string;
}

export interface StudentRequestDto {
    name: string;
    admissionNumber: number;
    classId: number;
    lin: string;
    dob: string;
    gender: Gender;
}

export interface SchoolSubject {
    id: number;
    name: string;
    description: string;
    schoolClassId: number;
    schoolClassName: string;
}

export interface SubjectRequestDto {
    name: string;
    description: string;
    schoolClassId: number;
    schoolClassName: string;
}

export interface SubjectUpdateDto {
    id: number;
    name: string;
    description: string;
    schoolClassId: number;
    schoolClassName: string;
}

export interface GradingStepDto {
    id: number;
    grade: string;
    minScore: number;
    maxScore: number;
    remark: string;
}

export interface GradingScaleDto {
    id: number;
    name: string;
    schoolId: number;
    steps: GradingStepDto[];
}