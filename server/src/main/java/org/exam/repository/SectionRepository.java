package org.exam.repository;

import org.exam.model.Exam;
import org.exam.model.Section;
import org.exam.model.Section.SectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByExam(Exam exam);
    Optional<Section> findByExamAndSectionType(Exam exam, SectionType sectionType);
}
