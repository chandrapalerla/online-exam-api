package org.exam.repository;

import org.exam.model.Question;
import org.exam.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySection(Section section);
    long countBySection(Section section);
}
