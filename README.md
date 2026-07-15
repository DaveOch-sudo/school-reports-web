# School Reports API

A multi-tenant Spring Boot REST API that streamlines learner report processing for schools — from entering subject scores to generating formatted report cards.

## What It Does

Schools register on the platform and manage their own isolated data: classes, students, subjects, marksheets, grading scales, and report templates. Teachers enter marks per subject per exam. The system compiles them into a general marksheet (ranked results across all subjects) and generates printable report cards using the school's configured template.

## Tech Stack

- Java 21 / Spring Boot 3.5
- Spring Data JPA + Hibernate
- MySQL
- Lombok
- Jackson (JSON serialization, bundled via spring-boot-starter-web)

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
├── GeneralMarksheet      (compiled results, unique: class + term + exam + year)
│   └── GeneralStudentResult
│       └── SubjectResult (snapshot of score + grade per subject)
└── ReportRun             (report card generation batch, unique: class + year + term)
    └── ReportCard        (one per student, unique: run + student)
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
| `ReportRun` | `(schoolClass, academicYear, term)` |
| `ReportCard` | `(reportRun, student)` |

### Marksheet Lifecycle

```
DRAFT → SUBMITTED → GRADED
```

- **DRAFT** — marks being entered
- **SUBMITTED** — marks finalised, ready for grading
- **GRADED** — grading scale applied, grades resolved per student

### ReportRun Lifecycle

```
DRAFT → APPROVED → PUBLISHED
```

- **DRAFT** — run created, per-student teacher comments being filled in
- **APPROVED** — headteacher signed off, all comments locked
- **PUBLISHED** — PDFs can be generated and downloaded

### GeneralMarksheet vs Marksheet

`Marksheet` is per-subject (e.g. Mathematics, Term 1, BOT). Once all subject marksheets for a class/term/exam are graded, a `GeneralMarksheet` is compiled — it aggregates every student's scores across all subjects, calculates totals, averages, and positions, and snapshots the result in `GeneralStudentResult` + `SubjectResult`. This snapshot is intentional: report cards must not change if marks are later edited.

### ReportRun and Template Snapshotting

When a `ReportRun` is created, the chosen `ReportTemplate`'s layout fields are serialised to JSON and stored in `ReportRun.templateSnapshot`. This means future edits to the template in the designer do not retroactively alter published report cards. Each run's layout is frozen at creation time.

A run can reference one `GeneralMarksheet` (single-exam card) or multiple (combined card, e.g. BOT + MID + EOT columns on a single page).

### Exam Types

| Value | Meaning |
|---|---|
| `BOT` | Beginning of Term |
| `MID` | Mid-Term |
| `EOT` | End of Term |
| `TEST` | Ad-hoc test |

## API Endpoints

### Schools
| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/schools` | Register a school |
| `GET` | `/api/v1/schools/{id}` | Get school by ID |
| `PUT` | `/api/v1/schools/{id}` | Update school |

### Classes
| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/classes` | Create a class |
| `GET` | `/api/v1/classes/{id}` | Get class by ID |
| `DELETE` | `/api/v1/classes/{id}` | Delete a class |

### Students
| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/students` | Add a student |
| `GET` | `/api/v1/students/{id}` | Get student by ID |
| `GET` | `/api/v1/students/class/{classId}` | List students in a class |

### Marksheets
| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/marksheets` | Create a marksheet |
| `GET` | `/api/v1/marksheets/{id}` | Get marksheet by ID |
| `PUT` | `/api/v1/marksheets/{id}/marks` | Replace all student marks |
| `PATCH` | `/api/v1/marksheets/{id}/submit` | Submit for grading |
| `PATCH` | `/api/v1/marksheets/{id}/grade` | Resolve grades |

### General Marksheets
| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/general-marksheets` | Compile a general marksheet |
| `GET` | `/api/v1/general-marksheets/{id}` | Get by ID |
| `GET` | `/api/v1/general-marksheets/class/{classId}` | All for a class |
| `GET` | `/api/v1/general-marksheets/landing` | Summary rows |
| `GET` | `/api/v1/general-marksheets/dashboard` | Aggregate stats |

### Report Runs
| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/report-runs` | Create a run (snapshots template, generates cards) |
| `GET` | `/api/v1/report-runs/{id}` | Get run by ID (includes all cards) |
| `GET` | `/api/v1/report-runs/class/{classId}` | All runs for a class |
| `PATCH` | `/api/v1/report-runs/{id}/approve` | Approve (locks comments) |
| `PATCH` | `/api/v1/report-runs/{id}/publish` | Publish (enables PDF download) |
| `PATCH` | `/api/v1/report-runs/{id}/headteacher-comment` | Update headteacher comment |

### Report Cards
| Method | Path | Description |
|---|---|---|
| `GET` | `/api/v1/report-cards/{id}` | Get a single card |
| `GET` | `/api/v1/report-cards/run/{runId}` | All cards for a run |
| `PATCH` | `/api/v1/report-cards/{id}/comment` | Update class teacher comment |

## Planned Features

- [ ] JWT authentication with roles (SUPER_ADMIN, SCHOOL_ADMIN, TEACHER)
- [ ] Report template designer API (field-level editing, preview)
- [ ] PDF report card generation (OpenPDF) — single card and bulk ZIP
- [ ] Multi-exam combined report card (BOT + MID + EOT on one page)
- [ ] Student bulk import via Excel (Apache POI)
- [ ] Global exception handler (`@ControllerAdvice`) with consistent error response shape

## Getting Started

### Prerequisites

- Java 21
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

## Documentation

- [`docs/report-run-frontend-guide.md`](docs/report-run-frontend-guide.md) — Step-by-step guide for frontend integration: marksheet grading → general marksheet compilation → report run creation → comment entry → approval → PDF download.
