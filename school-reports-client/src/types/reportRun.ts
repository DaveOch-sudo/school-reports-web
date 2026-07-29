import type {ReportRunStatus, Term} from "./enums.ts";

export interface ReportRun {
    id: number;
    schoolClassId: number;
    schoolClassName: string;
    academicYearId: number;
    academicYearLabel: string;
    term: Term;
    status: ReportRunStatus;
    generalMarksheetIds: number[];
    headteacherComment: string;
    createdAt: Date;
    approvedAt: Date;
    publishedAt: Date;
    cards: ReportCard[];
}

export interface ReportCard {
    id: number;
    reportRunId: number;
    studentId: number;
    studentName: string;
    classTeacherComment: string;
    runStatus: ReportRunStatus;
    generatedAt: Date;
}

export interface ReportRunRequestDto {
    schoolClassId: number;
    academicYearId: number;
    term: Term;
    generalMarksheetIds: number[];
    headteacherComment: string;
    createdById: number;
}