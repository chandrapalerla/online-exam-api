package org.exam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreationRequest {

    @NotBlank(message = "Question text cannot be empty")
    private String questionText;

    @NotBlank(message = "Question type must be specified")
    private String questionType;

    private Integer marks = 1;

    private List<QuestionOptionRequest> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionOptionRequest {
        @NotBlank(message = "Option text cannot be empty")
        private String optionText;

        @NotNull(message = "Must specify if option is correct")
        private Boolean isCorrect;
    }
}
