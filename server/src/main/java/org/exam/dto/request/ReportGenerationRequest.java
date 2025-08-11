package org.exam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerationRequest {

    @NotNull(message = "Exam ID must be provided")
    private Long examId;

    private String college;

    private PassingCriteria passingCriteria;

    private Boolean includeStatistics = true;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassingCriteria {
        private Integer aptitude = 10;
        private Integer reasoning = 10;
        private Integer coding = 10;
    }
}
