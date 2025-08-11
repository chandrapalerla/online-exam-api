package org.exam.repository;

import org.exam.model.ExamAttempt;
import org.exam.model.Student;
import org.exam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByStudent(Student student);
    List<ExamAttempt> findByExam(Exam exam);
    Optional<ExamAttempt> findByExamAndStudentAndIsCompletedFalse(Exam exam, Student student);
    boolean existsByExamAndStudent(Exam exam, Student student);
}
