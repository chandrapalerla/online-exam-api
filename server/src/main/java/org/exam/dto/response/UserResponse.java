package org.exam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;

    // Student specific fields
    private String studentId;
    private String branch;
    private String academicYear;

    // Admin specific fields
    private String department;
}
