package org.exam.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exam_reports")
public class ExamReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by", nullable = false)
    private User generatedBy;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(name = "report_path")
    private String reportPath;

    @Column(name = "college")
    private String college;

    @Column(name = "aptitude_passing_marks")
    private Integer aptitudePassingMarks;

    @Column(name = "reasoning_passing_marks")
    private Integer reasoningPassingMarks;

    @Column(name = "coding_passing_marks")
    private Integer codingPassingMarks;
}
