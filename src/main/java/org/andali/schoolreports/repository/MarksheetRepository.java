package org.andali.schoolreports.repository;

import org.andali.schoolreports.model.Marksheet;
import org.andali.schoolreports.model.enums.ExamType;
import org.andali.schoolreports.model.enums.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarksheetRepository extends JpaRepository<Marksheet, Long> {
    Marksheet findMarksheetByName(String name);

    void deleteByName(String name);

    List<Marksheet> findAllBySchoolClass_Id(Long schoolClassId);

    List<Marksheet> findAllByTerm(Term term);

    List<Marksheet> findAllBySchoolSubject_Id(Long schoolSubjectId);

    @Query("""
    SELECT m FROM Marksheet m
    LEFT JOIN FETCH m.studentMarks sm
    LEFT JOIN FETCH sm.student
    WHERE m.schoolClass.id = :classId
    AND m.schoolSubject.id = :subjectId
    AND m.term = :term
    AND m.examType = :examType
""")
    Marksheet findByClassAndSubjectAndTermAndExam(
            @Param("classId") Long classId,
            @Param("subjectId") Long subjectId,
            @Param("term") Term term,
            @Param("examType") ExamType examType
    );

    @Query("""
    SELECT m FROM Marksheet m
    LEFT JOIN FETCH m.studentMarks sm
    LEFT JOIN FETCH sm.student
    WHERE m.id = :id
""")
    Marksheet findMarksheetById(@Param("id") Long id);
}
