import type {ExamType} from "./enums.ts";

export interface ReportTemplate {
    id: number;
    schoolId: number;
    name: string;
    logoUrl: string;
    schoolNameOverride: string;
    showPosition: boolean;
    showAverage: boolean;
    showGrade: boolean;
    showRemark: boolean;
    showClassTeacherComment: boolean;
    showHeadTeacherComment: boolean;
    footerText: string;
    headerColor: string;
    includedExams: ExamType[];
}