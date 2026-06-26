# School Reports API

A multi-tenant Spring Boot REST API that streamlines learner report processing for schools — from entering subject scores to generating formatted report cards.

## What It Does

Schools register on the platform and manage their own isolated data: classes, students, subjects, marksheets, grading scales, and report templates. Teachers enter marks per subject per exam. The system compiles them into a general marksheet (ranked results across all subjects) and generates printable report cards using the school's configured template.

## Tech Stack

- Java 24 / Spring Boot 3.5
- Spring Data JPA + Hibernate
- MySQL
- Lombok

## Database Design

### Multi-tenancy

Every school's data is isolated. `SchoolClass`, `GradingScale`, `AcademicYear`, and `ReportTemplate` all belong directly to a `School`. Students and marksheets are scoped through their class.

### Entity Overview

```
School
├── AcademicYear          (label e.g. "2025", scoped per school)
├── SchoolClass           (unique: school + className)
│   ├── Student           (unique: class + lin)
│   └── SchoolSubject
├── GradingScale          (belongs to school)
│   └── GradeStep         (grade label, min/max score, remark)
├── ReportTemplate        (layout config for report card PDF)
├── Marksheet             (one per subject + class + term + exam + year)
│   └── StudentMark       (unique: marksheet + student)
└── GeneralMarksheet      (compiled results, unique: class + term + exam + year)
    └── GeneralStudentResult
        └── SubjectResult (snapshot of score + grade per subject)
```

### Key Constraints

| Entity | Unique Constraint |
|---|---|
| `SchoolClass` | `(school, className)` |
| `Student` | `(schoolClass, lin)` |
| `Marksheet` | `(schoolClass, schoolSubject, term, examType, academicYear)` |
| `GeneralMarksheet` | `(schoolClass, term, examType, academicYear)` |
| `StudentMark` | `(marksheet, student)` |
| `AcademicYear` | `(school, label)` |

### Marksheet Lifecycle

```
DRAFT → SUBMITTED → GRADED
```

- **DRAFT** — marks being entered
- **SUBMITTED** — marks finalised, ready for grading
- **GRADED** — grading scale applied, grades resolved per student

### GeneralMarksheet vs Marksheet

`Marksheet` is per-subject (e.g. Mathematics, Term 1, BOT). Once all subject marksheets for a class/term/exam are graded, a `GeneralMarksheet` is compiled — it aggregates every student's scores across all subjects, calculates totals, averages, and positions, and snapshots the result in `GeneralStudentResult` + `SubjectResult`. This snapshot is intentional: report cards must not change if marks are later edited.

### Exam Types

| Value | Meaning |
|---|---|
| `BOT` | Beginning of Term |
| `MID` | Mid-Term |
| `EOT` | End of Term |
| `TEST` | Ad-hoc test |

## Planned Features

- [ ] JWT authentication with roles (SUPER_ADMIN, SCHOOL_ADMIN, TEACHER)
- [ ] REST controllers for all domains
- [ ] Report template designer API
- [ ] PDF report card generation (OpenPDF)
- [ ] Student bulk import via Excel (Apache POI)
- [ ] Academic year management

## Getting Started

### Prerequisites

- Java 24
- MySQL 8+
- Maven 3.9+

### Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/school_reports?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=<your_mysql_user>
spring.datasource.password=<your_mysql_password>
```

### Run

```bash
mvn spring-boot:run
```

The database schema is created automatically via `ddl-auto=update`.
