package org.andali.schoolreportsweb.subject;

import lombok.RequiredArgsConstructor;
import org.andali.schoolreportsweb.schoolclass.SchoolClass;
import org.andali.schoolreportsweb.schoolclass.SchoolClassRepository;
import org.andali.schoolreportsweb.subject.dto.SubjectRequestDto;
import org.andali.schoolreportsweb.subject.dto.SubjectResponseDto;
import org.andali.schoolreportsweb.subject.dto.SubjectUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolSubjectService {

    @Autowired
    private final SchoolSubjectRepository repository;
    @Autowired
    private final SchoolClassRepository schoolClassRepository;

    public List<SubjectResponseDto> getAllSubjects() {
        return repository.findAll()
                .stream()
                .map(SchoolSubjectMapper::toDto)
                .toList();
    }

    public List<SubjectResponseDto> getAllBySchoolClass(SchoolClass schoolClass) {
        return repository.findAllBySchoolClass(schoolClass)
                .stream()
                .map(SchoolSubjectMapper::toDto)
                .toList();
    }

    public SubjectResponseDto getById(Long id) {
        return repository.findById(id)
                .map(SchoolSubjectMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));
    }

    public SubjectResponseDto save(SubjectRequestDto dto) {
        SchoolSubject subject1 = repository.save(SchoolSubjectMapper.toEntity(dto));
        return SchoolSubjectMapper.toDto(subject1);
    }

    public SubjectResponseDto update(Long id, SubjectUpdateDto updateDto) {
        SchoolSubject currentSubject = repository.findById(id).get();
        currentSubject.setSchoolClass(schoolClassRepository.findById(id).get());
        currentSubject.setDescription(updateDto.getDescription());
        currentSubject.setName(updateDto.getName());

        repository.save(currentSubject);
        return SchoolSubjectMapper.toDto(currentSubject);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
