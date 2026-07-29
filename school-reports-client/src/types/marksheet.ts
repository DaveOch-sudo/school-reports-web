import type {ExamType, MarksheetStatus, Term} from "./enums.ts";

export interface Marksheet {
    id: number;
    name: string;
    schoolClassId: number;
    schoolClassName: string;
    schoolSubjectId: number;
    schoolSubjectName: string;
    academicYearId: number;
    academicYearLabel: string;
    gradingScaleOverrideId: number;
    term: Term;
    examType: ExamType;
    status: MarksheetStatus;
    createdAt: Date;
    updatedAt: Date;
    studentMarks: StudentMarkDto[];
    
}

export interface StudentMarkDto {
    id: number;
    studentId: number;
    studentName: string;
    score: number;
    grade: string;
    remark: string;
}

export interface GeneralMarksheet {
    id: number;
    schoolClassId: number;
    schoolClassName: string;
    academicYearId: number;
    academicYearLabel: string;
    term: Term;
    examType: ExamType;
    generatedAt: Date;
    totalStudents: number;
    totalSubjects: number;
    classHighestTotal: number;
    classLowestTotal: number;
    classAverageTotal: number;
    results: GeneralStudentResultDto[];
}

export interface GeneralStudentResultDto {
    id: number;
    studentId: number;
    studentName: string;
    admissionNumber: number;
    totalMarks: number;
    averageMarks: number;
    position: number;
    subjectResults: SubjectResultDto[];
}

export interface SubjectResultDto {
    id: number;
    subjectName: string;
    score: number;
    grade: string;
    remark: string;
}