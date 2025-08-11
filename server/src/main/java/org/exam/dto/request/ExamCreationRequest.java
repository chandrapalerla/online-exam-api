package org.exam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamCreationRequest {

    @NotBlank(message = "Exam title cannot be empty")
    private String title;

    private String description;

    @NotNull(message = "Start time must be provided")
    private LocalDateTime startTime;

    @NotNull(message = "End time must be provided")
    private LocalDateTime endTime;

    private Integer durationMinutes = 60;

    private List<SectionRequest> sections;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionRequest {
        @NotBlank(message = "Section type cannot be empty")
        private String sectionType;

        @NotBlank(message = "Section title cannot be empty")
        private String title;

        private String description;

        private Integer passingMarks = 0;
    }
}
