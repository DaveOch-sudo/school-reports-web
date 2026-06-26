package org.andali.schoolreportsweb.service;

import org.andali.schoolreportsweb.model.GradingScale;
import org.andali.schoolreportsweb.repository.GradingScaleRepository;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GradingScaleService {
    private final GradingScaleRepository gradingScaleRepository;

    public GradingScaleService(GradingScaleRepository gradingScaleRepository) {
        this.gradingScaleRepository = gradingScaleRepository;
    }


    public List<GradingScale> getAllGradingScales() {
        return gradingScaleRepository.findAllWithSteps();
    }

    public GradingScale addGradingScale(GradingScale gradingScale) {
        return gradingScaleRepository.save(gradingScale);
    }

    public GradingScale getGradingScaleById(Long id) {
        return gradingScaleRepository.findGradingScaleById(id);
    }
}
