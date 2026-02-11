package org.andali.schoolreports.repository;

import org.andali.schoolreports.model.GeneralMarksheet;
import org.andali.schoolreports.model.SchoolClass;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralMarksheetRepository extends JpaRepository<GeneralMarksheet, Long> {
    boolean existsBySchoolClassAndTermAndExamType(SchoolClass schoolClass, Term term, ExamType examType);
}
