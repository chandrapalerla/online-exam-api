package org.exam.repository;

import org.exam.model.ExamReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamReportRepository extends JpaRepository<ExamReport, Long> {
}
