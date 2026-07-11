package org.andali.schoolreportsweb.year;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.school.School;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    public AcademicYear create(AcademicYear academicYear) {
        return academicYearRepository.save(academicYear);
    }

    public AcademicYear getById(Long id) {
        return academicYearRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("AcademicYear not found: " + id));
    }

    public List<AcademicYear> getAllBySchool(School school) {
        return academicYearRepository.findAllBySchool(school);
    }

    public AcademicYear getCurrentYear(School school) {
        return academicYearRepository.findBySchoolAndIsCurrentTrue(school)
                .orElseThrow(() -> new IllegalStateException("No current academic year set for school: " + school.getId()));
    }

    /** Sets the given year as current and clears the flag on all others for that school. */
    @Transactional
    public AcademicYear setAsCurrent(Long id, School school) {
        academicYearRepository.findAllBySchool(school)
                .forEach(y -> { y.setCurrent(false); academicYearRepository.save(y); });
        AcademicYear year = getById(id);
        year.setCurrent(true);
        return academicYearRepository.save(year);
    }

    public void delete(Long id) {
        academicYearRepository.deleteById(id);
    }
}
