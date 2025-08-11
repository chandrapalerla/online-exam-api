package org.exam.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Student ID cannot be empty")
    private String studentId;

    @NotBlank(message = "Full name cannot be empty")
    private String fullName;

    @NotBlank(message = "Branch cannot be empty")
    private String branch;

    @NotBlank(message = "Academic year cannot be empty")
    private String academicYear;
}
