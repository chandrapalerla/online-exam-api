package org.exam.service;

import org.exam.dto.request.LoginRequest;
import org.exam.dto.request.RegistrationRequest;
import org.exam.dto.response.AuthResponse;
import org.exam.dto.response.UserResponse;
import org.exam.exception.AuthenticationException;
import org.exam.model.Student;
import org.exam.model.User;
import org.exam.repository.StudentRepository;
import org.exam.repository.UserRepository;
import org.exam.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registerStudent(RegistrationRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email already registered");
        }

        // Check if student ID already exists
        if (studentRepository.existsByStudentId(request.getStudentId())) {
            throw new AuthenticationException("Student ID already registered");
        }

        // Create new student
        Student student = new Student();
        student.setEmail(request.getEmail());
        student.setFullName(request.getFullName());
        student.setRole(User.Role.STUDENT);
        student.setStudentId(request.getStudentId());
        student.setBranch(request.getBranch());
        student.setAcademicYear(request.getAcademicYear());

        // Save the student
        Student savedStudent = studentRepository.save(student);

        // Generate JWT token
        String token = jwtUtil.generateTokenForUser(savedStudent);

        // Create user response
        UserResponse userResponse = UserResponse.builder()
                .id(savedStudent.getId())
                .email(savedStudent.getEmail())
                .fullName(savedStudent.getFullName())
                .role(savedStudent.getRole().name())
                .studentId(savedStudent.getStudentId())
                .branch(savedStudent.getBranch())
                .academicYear(savedStudent.getAcademicYear())
                .build();

        // Return auth response with token and user details
        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    public AuthResponse adminLogin(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Generate JWT token
            String token = jwtUtil.generateToken(authentication);

            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            if (user.getRole() != User.Role.ADMIN) {
                throw new AuthenticationException("Access denied. Admin role required.");
            }

            // Create user response
            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .build();

            // Return auth response with token and user details
            return AuthResponse.builder()
                    .token(token)
                    .user(userResponse)
                    .build();
        } catch (Exception e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }

    public String getGoogleAuthorizationUrl() {
        // This would be implemented to generate the Google OAuth URL
        // In a real implementation, you would use OAuth2AuthorizedClientManager
        return "https://accounts.google.com/o/oauth2/auth?client_id=YOUR_CLIENT_ID&redirect_uri=YOUR_REDIRECT_URI&response_type=code&scope=email%20profile";
    }

    public AuthResponse processGoogleCallback(String code) {
        // This would be implemented to process the code from Google
        // Exchange code for token, validate token, get user info
        // Create or update user in the database

        // For demonstration purposes, we'll just return a mock response
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .email("student@example.com")
                .fullName("Google User")
                .role("STUDENT")
                .build();

        return AuthResponse.builder()
                .token("mock-jwt-token")
                .user(userResponse)
                .build();
    }
}
