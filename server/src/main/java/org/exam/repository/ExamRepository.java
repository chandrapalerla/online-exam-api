package org.exam.repository;

import org.exam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByIsActiveTrueAndStartTimeBefore(LocalDateTime now);
    List<Exam> findByIsActiveTrueAndEndTimeAfter(LocalDateTime now);
}
