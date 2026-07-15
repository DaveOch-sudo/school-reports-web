# Report Run — Frontend Integration Guide

This document describes the full flow from compiling subject marks to downloading
finished report card PDFs. Follow these steps in order; each step has prerequisites
that must be satisfied before it can proceed.

---

## Overview

```
Marksheets (per subject) → GRADED
        ↓
GeneralMarksheet compiled   (one per class/term/exam)
        ↓
ReportRun created           (snapshots template, generates cards)
        ↓
Teacher comments filled in  (one per student)
        ↓
Run APPROVED                (headteacher sign-off, comments locked)
        ↓
Run PUBLISHED               (PDFs available for download)
```

---

## Step 1 — Ensure all subject marksheets are GRADED

Before compiling a GeneralMarksheet, every subject marksheet for the target
class/term/exam combination must reach `GRADED` status.

**Marksheet lifecycle:**
```
DRAFT → SUBMITTED → GRADED
```

| Action | Endpoint |
|---|---|
| Submit a marksheet | `PATCH /api/v1/marksheets/{id}/submit` |
| Grade a marksheet | `PATCH /api/v1/marksheets/{id}/grade` |
| List marksheets for a class | `GET /api/v1/marksheets/class/{classId}` |

Show a checklist in the UI: one row per subject, with the current status badge.
Block the "Compile General Marksheet" button until all rows show `GRADED`.

---

## Step 2 — Compile a GeneralMarksheet

Once all subject marksheets are `GRADED`, compile the class-wide result snapshot.

```
POST /api/v1/general-marksheets
Content-Type: application/json

{
  "schoolClassId": 1,
  "term": "TERM_1",
  "examType": "BOT",
  "academicYearId": 2
}
```

**Success:** `201 Created` — returns the compiled `GeneralMarksheetResponseDto` with
ranked student results.

**Errors:**
- `409 Conflict` — a GeneralMarksheet already exists for this combination.
- `422 Unprocessable Entity` — one or more subject marksheets are not yet `GRADED`.

For a **combined EOT report card** (BOT + MID + EOT on one card) you will need to
compile a separate GeneralMarksheet for each exam type. Note the returned `id` for
each — you will supply all of them when creating the ReportRun.

---

## Step 3 — Design / select a ReportTemplate

The school admin configures the report card layout in the template designer.

Key template fields that affect rendering:

| Field | Effect |
|---|---|
| `showPosition` | Prints class rank on the card |
| `showAverage` | Prints the student's average score |
| `showGrade` | Prints grade label (e.g. "A", "B+") per subject |
| `showRemark` | Prints remark (e.g. "Excellent") per subject |
| `showClassTeacherComment` | Shows the teacher comment section |
| `showHeadteacherComment` | Shows the headteacher comment section |
| `headerColor` | Hex colour for the report header bar |
| `logoUrl` | School logo URL embedded in the header |
| `footerText` | Text printed at the bottom of each page |
| `includedExams` | Which exam types appear as columns on the card |

> **Important:** The template is snapshotted at ReportRun creation time. Changes
> to the template after that point do not affect existing runs or their PDFs.
> Make sure the template is finalised before creating the run.

```
GET  /api/v1/report-templates/school/{schoolId}   — list templates
POST /api/v1/report-templates                     — create template
PUT  /api/v1/report-templates/{id}                — update template
```

Note the `id` of the template you want to use in the next step.

---

## Step 4 — Create a ReportRun

This is the central action. It:
1. Snapshots the chosen template (freezes layout).
2. Creates one `ReportCard` per student in the class.
3. Sets run status to `DRAFT`.

```
POST /api/v1/report-runs
Content-Type: application/json

{
  "schoolClassId": 1,
  "academicYearId": 2,
  "term": "TERM_1",
  "generalMarksheetIds": [10],
  "templateId": 3,
  "headteacherComment": "Well done to all students this term.",
  "createdById": 5
}
```

For a **combined card** supply multiple marksheet IDs:
```json
"generalMarksheetIds": [10, 11, 12]
```

**Success:** `201 Created` — returns the full `ReportRunResponseDto` including all
generated cards.

**Errors:**
- `409 Conflict` — a run already exists for this class/year/term.
- `400 Bad Request` — no students in the class, or invalid IDs.

---

## Step 5 — Fill in class teacher comments

While the run is `DRAFT`, teachers enter a comment for each student.

```
PATCH /api/v1/report-cards/{cardId}/comment
Content-Type: application/json

{ "comment": "David is a hardworking student who shows great improvement." }
```

To list all cards for a run (to render the comment-entry form):

```
GET /api/v1/report-cards/run/{runId}
```

Response per card:
```json
{
  "id": 42,
  "studentId": 7,
  "studentName": "David Ochwoda",
  "classTeacherComment": null,
  "runStatus": "DRAFT",
  "generatedAt": "2026-07-15T10:00:00"
}
```

The headteacher comment (class-wide) can also be updated while `DRAFT`:

```
PATCH /api/v1/report-runs/{runId}/headteacher-comment
Content-Type: application/json

{ "comment": "The class has performed admirably this term." }
```

---

## Step 6 — Approve the run

When the headteacher has reviewed all comments, approve the run.
This **locks all comments** — no further edits are allowed.

```
PATCH /api/v1/report-runs/{runId}/approve
```

**Success:** `200 OK` — run status changes to `APPROVED`, `approvedAt` is set.

**Error:** `400` if the run is not currently `DRAFT`.

---

## Step 7 — Publish the run

Publishing makes PDFs available for download.

```
PATCH /api/v1/report-runs/{runId}/publish
```

**Success:** `200 OK` — run status changes to `PUBLISHED`, `publishedAt` is set.

**Error:** `400` if the run is not currently `APPROVED`.

---

## Step 8 — Download PDFs

> PDF generation endpoints are not yet implemented. They will be added in a
> future release. This section documents the planned interface.

Once published:

| Action | Endpoint (planned) |
|---|---|
| Download single student's PDF | `GET /api/v1/report-cards/{id}/pdf` |
| Download all students as a ZIP | `GET /api/v1/report-runs/{runId}/pdf/zip` |

The PDF renderer will read:
- Layout flags from `ReportRun.templateSnapshot` (the frozen snapshot)
- Student scores/grades/positions from `GeneralStudentResult` via `sourceMarksheets`
- Per-student comment from `ReportCard.classTeacherComment`
- Class-wide comment from `ReportRun.headteacherComment`

---

## Status Reference

### ReportRun status

| Status | Editable | PDF available |
|---|---|---|
| `DRAFT` | Yes — comments can be updated | No |
| `APPROVED` | No | No |
| `PUBLISHED` | No | Yes |

### Marksheet status

| Status | Meaning |
|---|---|
| `DRAFT` | Marks being entered |
| `SUBMITTED` | Marks finalised, ready for grading |
| `GRADED` | Grades resolved and persisted |

---

## Error Handling Summary

| HTTP Status | Meaning |
|---|---|
| `400 Bad Request` | Invalid lifecycle transition or missing data |
| `404 Not Found` | Entity with given ID does not exist |
| `409 Conflict` | Duplicate — entity already exists for this combination |
| `422 Unprocessable Entity` | Precondition failed (e.g. not all marksheets graded) |
