package org.andali.schoolreports.service;

import org.andali.schoolreports.dto.MarksheetsLandingDto;
import org.andali.schoolreports.model.GradeStep;
import org.andali.schoolreports.model.GradingScale;
import org.andali.schoolreports.model.Marksheet;
import org.andali.schoolreports.model.StudentMark;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.Term;
import org.andali.schoolreports.repository.MarksheetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarksheetService {

    private final MarksheetRepository marksheetRepository;
    private final GradingScaleService gradingScaleService;

    public MarksheetService(MarksheetRepository marksheetRepository, GradingScaleService gradingScaleService) {
        this.marksheetRepository = marksheetRepository;
        this.gradingScaleService = gradingScaleService;
    }

    // ── Basic CRUD ──────────────────────────────────────────────────────

    public List<Marksheet> getAllMarksheets() {
        return marksheetRepository.findAll();
    }

    public Marksheet getMarksheetById(Long id) {
        return marksheetRepository.findMarksheetById(id);
    }

    public Marksheet getMarksheetByName(String name) {
        return marksheetRepository.findMarksheetByName(name);
    }

    @Transactional
    public Marksheet addNewMarksheet(Marksheet marksheet) {
        resolveAllGrades(marksheet);
        return marksheetRepository.save(marksheet);
    }

    @Transactional
    public Marksheet updateMarksheet(Marksheet marksheet) {
        resolveAllGrades(marksheet);
        return marksheetRepository.save(marksheet);
    }

    public void deleteMarksheetById(Long id) {
        marksheetRepository.deleteById(id);
    }

    public void deleteMarksheetByName(String name) {
        marksheetRepository.deleteByName(name);
    }

    // ── Filtered queries ────────────────────────────────────────────────

    public List<Marksheet> getMarksheetsByClassId(Long classId) {
        return marksheetRepository.findAllBySchoolClass_Id(classId);
    }

    public List<Marksheet> getMarksheetByTerm(Term term) {
        return marksheetRepository.findAllByTerm(term);
    }

    public List<Marksheet> getMarksheetBySubject(Long subjectId) {
        return marksheetRepository.findAllBySchoolSubject_Id(subjectId);
    }

    // ── Grade resolution ────────────────────────────────────────────────

    /**
     * Looks up the score against the marksheet's GradingScale and sets
     * the grade and remark on each StudentMark.
     *
     * Called automatically on save/update so grades stay in sync with scores.
     */
    public void resolveAllGrades(Marksheet marksheet) {
        GradingScale scale = gradingScaleService.getGradingScaleById(marksheet.getGradingScale().getId());

        for (StudentMark mark : marksheet.getStudentMarks()) {
            GradeStep step = findMatchingStep(scale, mark.getScore());

            if (step != null) {
                mark.setGrade(step.getGrade());
                mark.setRemark(step.getRemark());
            } else {
                // Score falls outside all defined ranges
                mark.setGrade("N/A");
                mark.setRemark("Score out of grading range");
            }
        }
    }

    /**
     * Finds the GradeStep whose min–max range contains the given score.
     * Returns null if no step matches (i.e. the grading scale has a gap or the score is out of bounds).
     */
    private GradeStep findMatchingStep(GradingScale scale, int score) {
        return scale.getSteps().stream()
                .filter(step -> score >= step.getMinScore() && score <= step.getMaxScore())
                .findFirst()
                .orElse(null);
    }

    // ── DTO mapping ─────────────────────────────────────────────────────

    public List<MarksheetsLandingDto> allMarksheetsToDto() {
        List<Marksheet> marksheets = marksheetRepository.findAll();
        List<MarksheetsLandingDto> dtos = new ArrayList<>();

        for (Marksheet marksheet : marksheets) {
            MarksheetsLandingDto dto = new MarksheetsLandingDto();
            dto.setMarksheetId(marksheet.getId());
            dto.setMarksheetName(marksheet.getName());
            dto.setExamType(marksheet.getExamType());
            dto.setTerm(marksheet.getTerm());
            dto.setLastUpdated(marksheet.getUpdatedAt());
            dto.setCreated(marksheet.getCreatedAt());
            dto.setStatus(marksheet.getStatus());
            dto.setSchoolClass(marksheet.getSchoolClass());
            dto.setSchoolSubject(marksheet.getSchoolSubject());
            dtos.add(dto);
        }

        return dtos;
    }

    public Marksheet findByClassAndSubjectAndTermAndExam(Long id, Long id1, Term selectedTerm, ExamType selectedExam) {
        return marksheetRepository.findByClassAndSubjectAndTermAndExam(id, id1, selectedTerm, selectedExam);
    }
}