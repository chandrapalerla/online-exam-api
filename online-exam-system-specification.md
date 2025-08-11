 # Online Exam Web Application - Comprehensive Specification

## 1. System Overview

This document outlines the specifications for an online examination platform designed for educational institutions. The system enables students to take exams and administrators to manage exams and generate comprehensive reports.

## 2. Core Technologies

- **Frontend**: React.js
- **Backend**: Spring Boot (JDK 17)
- **Database**: MySQL
- **Authentication**: OAuth 2.0 Resource Server (Gmail login)

## 3. Detailed Functional Requirements

### 3.1. User Registration

#### Student Registration
- Students can register using either their unique Student ID or Email Address
- Registration captures:
  - Full name
  - Branch/Department
  - Academic year
- Validation requirements:
  - Student ID must follow institution format and be unique
  - Email must be valid and unique
  - All required fields must be completed
- Upon successful registration, students receive a confirmation email
- Students can immediately log in after registration is complete

### 3.2. User Login

- Primary authentication mechanism: OAuth 2.0 with Gmail
- Users authenticate using their registered email address
- Authentication flow:
  - User clicks "Login with Gmail" button
  - User is redirected to Google's authentication page
  - After successful authentication, user is redirected back to application
  - JWT token is stored for subsequent requests
- Administrator fallback authentication:
  - Username/password authentication with BCrypt password hashing
  - Password requirements: minimum 8 characters, combination of uppercase, lowercase, numbers, and symbols
  - Password reset functionality via email

### 3.3. Exam Launch & Structure

#### Exam Dashboard
- Upon login, students see a personalized dashboard with:
  - Available upcoming exams
  - Past exam attempts and results
  - Scheduled examination dates and times
  - Important notifications

#### Exam Structure
- Each exam is strictly divided into three sections:
  1. Aptitude (20 questions)
  2. Reasoning (20 questions)
  3. Coding (20 questions)
- Question types supported:
  - Multiple choice (single answer)
  - Multiple choice (multiple answers)
  - True/False
  - Short answer
  - Code writing (for coding section)

#### Navigation and Progress
- Intuitive navigation between questions within the current section
- Section progress indicator showing completed/remaining questions
- Section access control:
  - Default: Sequential section completion (configurable by administrators)
  - Option for administrators to allow free navigation between all sections
- Auto-save functionality for answers

### 3.4. Exam Environment Control

#### Security Features
- Enforced full-screen mode when exam begins
- Tab/window switching detection:
  - Using visibilitychange API
  - Using blur/focus events
  - Warning messages to students when focus loss is detected
- Activity logging:
  - All focus loss events are logged with timestamps
  - Excessive violations trigger notifications to administrators
- Copy/paste prevention within exam interface
- Browser back button disabled during exam
- Browser refresh handling with session persistence

#### Limitations Notice
- Clear disclaimer explaining the best-effort nature of client-side controls
- Warning about integrity pledge and consequences of violations
- Administrator review system for flagged exam attempts

### 3.5. Exam Timer & Submission

#### Timer Features
- 60-minute countdown timer displayed prominently
- Visual alerts when 15, 10, and 5 minutes remain
- Timer persistence across page refreshes or accidental closures
- Server-side validation of exam duration

#### Submission Process
- Automatic submission when timer expires
- Manual submission option with confirmation dialog
- Partial submission handling:
  - All answered questions are saved
  - Unanswered questions marked as skipped
- Submission confirmation with summary of answered/unanswered questions
- Receipt/confirmation page after submission

### 3.6. Administrator Reporting

#### Report Generation
- Admin dashboard for report management
- Filtering options:
  - By institution/college
  - By date range
  - By exam type
  - By student cohort/batch

#### Report Content
- Generated in PDF format with institutional branding
- Student details:
  - Student ID
  - Full name
  - Branch/Department
  - Academic year
- Performance metrics:
  - Section-wise scores (Aptitude, Reasoning, Coding)
  - Total score
  - Pass/Fail status based on configured thresholds
- Statistical analysis:
  - Average scores
  - Distribution graphs
  - Comparative performance metrics

#### Pass/Fail Configuration
- Admin interface to set passing thresholds:
  - Minimum marks required for each section
  - Option for overall passing score requirement
- Customizable passing criteria per exam
- Student passes only when all sectional cutoffs are met

## 4. Non-Functional Requirements

### 4.1. Security

- OAuth 2.0 implementation with secure configuration
- JWT token-based authentication with appropriate expiration
- HTTPS/TLS for all communications
- Input validation for all form fields
- Protection against common vulnerabilities:
  - SQL Injection
  - Cross-Site Scripting (XSS)
  - Cross-Site Request Forgery (CSRF)
  - Session fixation
- Secure password storage with BCrypt
- Role-based access control (RBAC)
- Regular security audits and penetration testing

### 4.2. Performance

- Page load time < 2 seconds
- API response time < 500ms for standard requests
- Support for concurrent users:
  - Minimum 100 simultaneous exam takers
  - Up to 1000 total daily users
- Efficient database query optimization
- Caching strategies for static content
- Image and asset optimization

### 4.3. Scalability

- Horizontally scalable architecture
- Database connection pooling
- Microservices approach for key components
- Cloud-ready deployment configuration
- Load balancer compatibility

### 4.4. Usability

- Responsive design for all screen sizes
- Accessibility compliance (WCAG 2.1 AA)
- Intuitive navigation and clear instructions
- Consistent UI elements and color scheme
- Helpful error messages and user guidance
- Multi-language support (future expansion)

### 4.5. Reliability

- System availability of 99.9% during scheduled exams
- Comprehensive error logging and monitoring
- Automated backup strategy
- Disaster recovery plan
- Graceful degradation for non-critical features

### 4.6. Maintainability

- Clean, documented code following best practices
- Comprehensive API documentation
- Modular architecture for easy updates
- Continuous integration/continuous deployment support
- Version control for all code and configurations

## 5. High-Level Architecture

```
┌─────────────────┐     ┌──────────────────────────────────────┐     ┌─────────────┐
│                 │     │                                      │     │             │
│   React.js      │◄────┤   Spring Boot Application            │◄────┤   MySQL     │
│   Frontend      │     │                                      │     │   Database  │
│                 │────►│   - Controllers                      │────►│             │
└─────────────────┘     │   - Services                         │     └─────────────┘
                        │   - Repositories                     │
                        │   - Security                         │
                        │                                      │
                        └───────────────┬──────────────────────┘
                                       │
                                       │
                                       ▼
                        ┌──────────────────────────────────┐
                        │                                  │
                        │   Google OAuth 2.0 Provider      │
                        │                                  │
                        └──────────────────────────────────┘
```

## 6. Spring Boot Project Structure

```
org.exam
├── ExamApplication.java
├── config
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   └── OAuth2ResourceServerConfig.java
├── controller
│   ├── AuthController.java
│   ├── ExamController.java
│   ├── QuestionController.java
│   ├── StudentController.java
│   └── AdminController.java
├── service
│   ├── UserService.java
│   ├── ExamService.java
│   ├── QuestionService.java
│   ├── ReportService.java
│   └── SecurityService.java
├── repository
│   ├── UserRepository.java
│   ├── ExamRepository.java
│   ├── QuestionRepository.java
│   ├── AnswerRepository.java
│   └── ReportRepository.java
├── model
│   ├── User.java
│   ├── Student.java
│   ├── Admin.java
│   ├── Exam.java
│   ├── Section.java
│   ├── Question.java
│   ├── Answer.java
│   ├── ExamAttempt.java
│   └── Report.java
├── dto
│   ├── request
│   │   ├── RegistrationRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AnswerSubmissionRequest.java
│   │   └── ReportGenerationRequest.java
│   └── response
│       ├── UserResponse.java
│       ├── ExamResponse.java
│       ├── QuestionResponse.java
│       └── ReportResponse.java
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── AuthenticationException.java
└── util
    ├── PdfGenerator.java
    ├── JwtUtil.java
    └── ValidationUtil.java
```

## 7. MySQL Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255), -- BCrypt hash, nullable for OAuth users
    full_name VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Students Table
```sql
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    student_id VARCHAR(50) UNIQUE NOT NULL,
    branch VARCHAR(100) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Admins Table
```sql
CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    department VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Exams Table
```sql
CREATE TABLE exams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL DEFAULT 60,
    is_active BOOLEAN DEFAULT FALSE,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);
```

### Sections Table
```sql
CREATE TABLE sections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    section_type ENUM('APTITUDE', 'REASONING', 'CODING') NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    passing_marks INT NOT NULL DEFAULT 0,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);
```

### Questions Table
```sql
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    section_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MULTIPLE_CHOICE_SINGLE', 'MULTIPLE_CHOICE_MULTIPLE', 'TRUE_FALSE', 'SHORT_ANSWER', 'CODE') NOT NULL,
    marks INT NOT NULL DEFAULT 1,
    FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE
);
```

### Question Options Table
```sql
CREATE TABLE question_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);
```

### Exam Attempts Table
```sql
CREATE TABLE exam_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (exam_id) REFERENCES exams(id),
    FOREIGN KEY (student_id) REFERENCES students(id)
);
```

### Student Answers Table
```sql
CREATE TABLE student_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_text TEXT,
    is_correct BOOLEAN,
    marks_awarded DECIMAL(5,2) DEFAULT 0,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);
```

### Student Answer Options Table (for multiple choice)
```sql
CREATE TABLE student_answer_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_answer_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    FOREIGN KEY (student_answer_id) REFERENCES student_answers(id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES question_options(id)
);
```

### Exam Reports Table
```sql
CREATE TABLE exam_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    generated_by BIGINT NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    report_path VARCHAR(255),
    FOREIGN KEY (exam_id) REFERENCES exams(id),
    FOREIGN KEY (generated_by) REFERENCES users(id)
);
```

### Focus Loss Events Table
```sql
CREATE TABLE focus_loss_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    event_type VARCHAR(50) NOT NULL, -- 'TAB_SWITCH', 'WINDOW_BLUR', etc.
    duration_seconds INT DEFAULT 0,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE
);
```

## 8. API Endpoint Specification

### Authentication APIs

#### Register User
- **Endpoint**: `POST /api/auth/register`
- **Description**: Register a new student
- **Request Body**:
  ```json
  {
    "email": "student@example.com",
    "studentId": "S12345",
    "fullName": "John Smith",
    "branch": "Computer Science",
    "academicYear": "2025"
  }
  ```
- **Response**: 
  ```json
  {
    "id": 1,
    "email": "student@example.com",
    "fullName": "John Smith",
    "role": "STUDENT",
    "studentDetails": {
      "studentId": "S12345",
      "branch": "Computer Science",
      "academicYear": "2025"
    }
  }
  ```
- **Status Codes**:
  - 201: Created
  - 400: Bad Request
  - 409: Conflict (Email/StudentId already exists)

#### OAuth2 Login Initiation
- **Endpoint**: `GET /api/auth/oauth2/google`
- **Description**: Initiates OAuth2 login flow with Google
- **Response**: Redirects to Google OAuth2 login page
- **Status Codes**:
  - 302: Redirect to Google

#### OAuth2 Callback
- **Endpoint**: `GET /api/auth/oauth2/callback/google`
- **Description**: Callback endpoint after Google authentication
- **Response**:
  ```json
  {
    "token": "jwt-token-here",
    "user": {
      "id": 1,
      "email": "student@example.com",
      "fullName": "John Smith",
      "role": "STUDENT"
    }
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized

#### Admin Login
- **Endpoint**: `POST /api/auth/admin/login`
- **Description**: Login endpoint for admins using username/password
- **Request Body**:
  ```json
  {
    "email": "admin@example.com",
    "password": "securepassword"
  }
  ```
- **Response**:
  ```json
  {
    "token": "jwt-token-here",
    "user": {
      "id": 2,
      "email": "admin@example.com",
      "fullName": "Admin User",
      "role": "ADMIN"
    }
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized

### Student APIs

#### Get Available Exams
- **Endpoint**: `GET /api/student/exams`
- **Auth**: Required (Student)
- **Description**: Get list of available exams for the student
- **Response**:
  ```json
  {
    "upcoming": [
      {
        "id": 1,
        "title": "Midterm Examination",
        "description": "Computer Science midterm",
        "startTime": "2025-07-15T09:00:00Z",
        "endTime": "2025-07-15T12:00:00Z",
        "durationMinutes": 60
      }
    ],
    "past": [
      {
        "id": 2,
        "title": "Programming Quiz",
        "description": "Basic programming concepts",
        "completedOn": "2025-06-01T10:30:45Z",
        "score": 85,
        "passed": true
      }
    ]
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized

#### Start Exam
- **Endpoint**: `POST /api/student/exams/{examId}/start`
- **Auth**: Required (Student)
- **Description**: Start an exam attempt
- **Response**:
  ```json
  {
    "attemptId": 1,
    "examId": 1,
    "title": "Midterm Examination",
    "startTime": "2025-06-29T14:30:00Z",
    "durationMinutes": 60,
    "endTime": "2025-06-29T15:30:00Z",
    "currentSection": "APTITUDE"
  }
  ```
- **Status Codes**:
  - 201: Created
  - 400: Bad Request
  - 401: Unauthorized
  - 403: Forbidden (Not eligible or already attempted)

#### Get Section Questions
- **Endpoint**: `GET /api/student/attempts/{attemptId}/sections/{sectionType}/questions`
- **Auth**: Required (Student)
- **Description**: Get questions for a specific section in the exam
- **Path Parameters**:
  - attemptId: Exam attempt ID
  - sectionType: APTITUDE, REASONING, or CODING
- **Response**:
  ```json
  {
    "sectionType": "APTITUDE",
    "totalQuestions": 20,
    "questions": [
      {
        "id": 1,
        "questionText": "What is the result of 2+2?",
        "questionType": "MULTIPLE_CHOICE_SINGLE",
        "options": [
          {"id": 1, "optionText": "3"},
          {"id": 2, "optionText": "4"},
          {"id": 3, "optionText": "5"},
          {"id": 4, "optionText": "6"}
        ]
      },
      // More questions...
    ]
  }
  ```
- **Status Codes**:
  - 200: OK
  - 400: Bad Request
  - 401: Unauthorized
  - 403: Forbidden (Section not accessible yet)

#### Submit Answers
- **Endpoint**: `POST /api/student/attempts/{attemptId}/sections/{sectionType}/submit`
- **Auth**: Required (Student)
- **Description**: Submit answers for a section
- **Path Parameters**:
  - attemptId: Exam attempt ID
  - sectionType: APTITUDE, REASONING, or CODING
- **Request Body**:
  ```json
  {
    "answers": [
      {
        "questionId": 1,
        "selectedOptionIds": [2]
      },
      {
        "questionId": 2,
        "selectedOptionIds": [5, 7]
      },
      {
        "questionId": 3,
        "answerText": "This is a short answer"
      },
      // More answers...
    ]
  }
  ```
- **Response**:
  ```json
  {
    "sectionType": "APTITUDE",
    "submitted": true,
    "nextSection": "REASONING",
    "remainingSections": ["REASONING", "CODING"]
  }
  ```
- **Status Codes**:
  - 200: OK
  - 400: Bad Request
  - 401: Unauthorized
  - 403: Forbidden

#### Complete Exam
- **Endpoint**: `POST /api/student/attempts/{attemptId}/complete`
- **Auth**: Required (Student)
- **Description**: Complete the exam (manual submission)
- **Path Parameters**:
  - attemptId: Exam attempt ID
- **Response**:
  ```json
  {
    "examId": 1,
    "attemptId": 1,
    "completionTime": "2025-06-29T15:15:23Z",
    "submitted": true,
    "message": "Exam completed successfully"
  }
  ```
- **Status Codes**:
  - 200: OK
  - 400: Bad Request
  - 401: Unauthorized
  - 403: Forbidden

#### Log Focus Loss Event
- **Endpoint**: `POST /api/student/attempts/{attemptId}/events/focus-loss`
- **Auth**: Required (Student)
- **Description**: Log when a student loses focus during exam
- **Path Parameters**:
  - attemptId: Exam attempt ID
- **Request Body**:
  ```json
  {
    "eventType": "TAB_SWITCH",
    "durationSeconds": 5
  }
  ```
- **Response**: 
  ```json
  {
    "recorded": true,
    "warningLevel": "MEDIUM",
    "message": "Focus loss event recorded"
  }
  ```
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized

### Admin APIs

#### Create Exam
- **Endpoint**: `POST /api/admin/exams`
- **Auth**: Required (Admin)
- **Description**: Create a new exam
- **Request Body**:
  ```json
  {
    "title": "Final Examination",
    "description": "Computer Science final exam",
    "startTime": "2025-07-20T09:00:00Z",
    "endTime": "2025-07-20T12:00:00Z",
    "durationMinutes": 60,
    "sections": [
      {
        "sectionType": "APTITUDE",
        "title": "Aptitude Section",
        "description": "Basic aptitude questions",
        "passingMarks": 10
      },
      {
        "sectionType": "REASONING",
        "title": "Reasoning Section",
        "description": "Logical reasoning questions",
        "passingMarks": 10
      },
      {
        "sectionType": "CODING",
        "title": "Coding Section",
        "description": "Programming problems",
        "passingMarks": 10
      }
    ]
  }
  ```
- **Response**:
  ```json
  {
    "id": 3,
    "title": "Final Examination",
    "description": "Computer Science final exam",
    "startTime": "2025-07-20T09:00:00Z",
    "endTime": "2025-07-20T12:00:00Z",
    "durationMinutes": 60,
    "isActive": false,
    "sections": [
      {
        "id": 1,
        "sectionType": "APTITUDE",
        "title": "Aptitude Section",
        "passingMarks": 10
      },
      {
        "id": 2,
        "sectionType": "REASONING",
        "title": "Reasoning Section",
        "passingMarks": 10
      },
      {
        "id": 3,
        "sectionType": "CODING",
        "title": "Coding Section",
        "passingMarks": 10
      }
    ]
  }
  ```
- **Status Codes**:
  - 201: Created
  - 400: Bad Request
  - 401: Unauthorized
  - 403: Forbidden

#### Add Questions to Section
- **Endpoint**: `POST /api/admin/exams/{examId}/sections/{sectionId}/questions`
- **Auth**: Required (Admin)
- **Description**: Add questions to an exam section
- **Path Parameters**:
  - examId: Exam ID
  - sectionId: Section ID
- **Request Body**:
  ```json
  {
    "questions": [
      {
        "questionText": "What is the capital of France?",
        "questionType": "MULTIPLE_CHOICE_SINGLE",
        "marks": 1,
        "options": [
          {"optionText": "London", "isCorrect": false},
          {"optionText": "Paris", "isCorrect": true},
          {"optionText": "Berlin", "isCorrect": false},
          {"optionText": "Rome", "isCorrect": false}
        ]
      },
      {
        "questionText": "Write a function to find factorial of a number",
        "questionType": "CODE",
        "marks": 5
      }
    ]
  }
  ```
- **Response**:
  ```json
  {
    "sectionId": 1,
    "questionsAdded": 2,
    "totalQuestions": 10
  }
  ```
- **Status Codes**:
  - 201: Created
  - 400: Bad Request
  - 401: Unauthorized
  - 403: Forbidden

#### Generate Report
- **Endpoint**: `POST /api/admin/reports`
- **Auth**: Required (Admin)
- **Description**: Generate a report for an exam
- **Request Body**:
  ```json
  {
    "examId": 1,
    "college": "Engineering College",
    "passingCriteria": {
      "aptitude": 10,
      "reasoning": 10,
      "coding": 10
    },
    "includeStatistics": true
  }
  ```
- **Response**:
  ```json
  {
    "reportId": 1,
    "examId": 1,
    "generatedAt": "2025-06-29T16:45:12Z",
    "reportUrl": "/api/admin/reports/1/download",
    "totalStudents": 50,
    "passedStudents": 35,
    "failedStudents": 15
  }
  ```
- **Status Codes**:
  - 202: Accepted (Report generation in progress)
  - 400: Bad Request
  - 401: Unauthorized
  - 403: Forbidden

#### Download Report
- **Endpoint**: `GET /api/admin/reports/{reportId}/download`
- **Auth**: Required (Admin)
- **Description**: Download a generated PDF report
- **Path Parameters**:
  - reportId: Report ID
- **Response**: PDF file download
- **Status Codes**:
  - 200: OK
  - 401: Unauthorized
  - 403: Forbidden
  - 404: Not Found

## 9. React.js Component Design

### Core Components

#### Authentication Components
- `LoginPage` - Handles OAuth login and admin login
- `RegistrationPage` - Student registration form
- `PrivateRoute` - Route protection based on authentication status

#### Student Components
- `StudentDashboard` - Home page for students after login
- `ExamList` - Lists available and past exams
- `ExamLauncher` - Responsible for initiating the exam
- `ExamEnvironment` - The full-screen exam container
  - `ExamTimer` - Countdown timer component
  - `SectionNavigation` - For navigating between questions in a section
  - `QuestionRenderer` - Renders different question types
    - `MultipleChoiceQuestion`
    - `ShortAnswerQuestion`
    - `CodingQuestion` (with code editor)
  - `ExamSubmission` - Handles exam submission process

#### Admin Components
- `AdminDashboard` - Home page for administrators
- `ExamCreator` - Interface for creating new exams
- `QuestionManager` - For adding and managing questions
- `ReportGenerator` - Interface for generating reports
- `ReportViewer` - Displays report preview
- `StudentResults` - Shows individual student results

## 10. Key Implementation Considerations

### OAuth 2.0 Integration
- Implement Spring Security OAuth2 Resource Server
- Configure proper CORS settings for frontend/backend integration
- Securely store and validate JWT tokens
- Handle token refresh and expiration

### Exam Environment Security
- Use Pointer Lock API and Page Visibility API for focus detection
- Implement fullscreen mode using Fullscreen API
- Add browser event listeners to detect tab switching
- Create periodic heartbeat requests to verify client status

### Real-time Timer Implementation
- Server-side timestamp synchronization
- WebSockets for real-time updates
- Local storage backup for client-side timer state
- Graceful handling of connection loss

### PDF Report Generation
- Use a Java library like iText or Apache PDFBox
- Implement asynchronous report generation for large reports
- Create templates with institutional branding
- Include charts and graphs using a charting library

### Question Types and Grading
- Implement different scoring mechanisms for different question types
- Auto-grading for objective questions (multiple choice, true/false)
- Manual or AI-assisted grading for coding questions
- Support for partial marks in complex questions

## 11. Security Considerations

- Regular security audits and penetration testing
- Input validation on all form fields
- Protection against XSS using appropriate escaping
- CSRF protection with tokens
- Rate limiting for sensitive endpoints
- Proper error handling that doesn't reveal sensitive information
- Secure storage of credentials and tokens
- Regular dependency updates to patch vulnerabilities

Swagger UI: http://localhost:8080/api/swagger-ui
OpenAPI documentation: http://localhost:8080/api/api-docs
