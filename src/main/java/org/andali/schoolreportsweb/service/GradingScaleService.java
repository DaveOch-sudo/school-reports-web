package org.andali.schoolreportsweb.service;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.model.GradeStep;
import org.andali.schoolreportsweb.model.GradingScale;
import org.andali.schoolreportsweb.model.School;
import org.andali.schoolreportsweb.repository.GradingScaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradingScaleService {

    private final GradingScaleRepository gradingScaleRepository;

    public GradingScale create(GradingScale gradingScale) {
        return gradingScaleRepository.save(gradingScale);
    }

    public GradingScale getById(Long id) {
        return gradingScaleRepository.findGradingScaleById(id);
    }

    public List<GradingScale> getAllBySchool(School school) {
        return gradingScaleRepository.findAllBySchool(school);
    }

    public GradingScale addStep(Long scaleId, GradeStep step) {
        GradingScale scale = getById(scaleId);
        scale.addStep(step);
        return gradingScaleRepository.save(scale);
    }

    public GradingScale removeStep(Long scaleId, GradeStep step) {
        GradingScale scale = getById(scaleId);
        scale.removeStep(step);
        return gradingScaleRepository.save(scale);
    }

    public void delete(Long id) {
        gradingScaleRepository.deleteById(id);
    }

    // kept for backward compatibility with MarksheetService
    public GradingScale getGradingScaleById(Long id) {
        return getById(id);
    }

    public List<GradingScale> getAllGradingScales() {
        return gradingScaleRepository.findAllWithSteps();
    }
}
