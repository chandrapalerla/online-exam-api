package org.exam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmissionRequest {

    @NotNull(message = "Answers cannot be null")
    private List<StudentAnswerRequest> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentAnswerRequest {
        @NotNull(message = "Question ID must be provided")
        private Long questionId;

        private List<Long> selectedOptionIds;

        private String answerText;
    }
}
