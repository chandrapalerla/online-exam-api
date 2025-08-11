package org.exam.service;

import org.exam.dto.request.AnswerSubmissionRequest;
import org.exam.dto.response.ExamResponse;
import org.exam.dto.response.QuestionResponse;
import org.exam.exception.ResourceNotFoundException;
import org.exam.model.*;
import org.exam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private StudentAnswerRepository studentAnswerRepository;

    public Map<String, List<ExamResponse>> getAvailableExams() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Ensure the user is a student
        if (user.getRole() != User.Role.STUDENT) {
            throw new ResourceNotFoundException("Student not found");
        }

        Student student = (Student) user;

        LocalDateTime now = LocalDateTime.now();

        // Get upcoming exams
        List<Exam> upcomingExams = examRepository.findByIsActiveTrueAndEndTimeAfter(now);

        // Convert to response objects
        List<ExamResponse> upcoming = upcomingExams.stream()
                .map(this::convertToExamResponse)
                .collect(Collectors.toList());

        // For past exams, we'd fetch exam attempts by the student
        // and create responses with results
        List<ExamResponse> past = new ArrayList<>(); // Implementation omitted for brevity

        Map<String, List<ExamResponse>> result = new HashMap<>();
        result.put("upcoming", upcoming);
        result.put("past", past);

        return result;
    }

    @Transactional
    public Map<String, Object> startExam(Long examId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // Check if student has already attempted this exam
        if (examAttemptRepository.existsByExamAndStudent(exam, student)) {
            throw new IllegalStateException("You have already attempted this exam");
        }

        // Check if exam is active and within time window
        LocalDateTime now = LocalDateTime.now();
        if (!exam.getIsActive() || now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            throw new IllegalStateException("Exam is not available at this time");
        }

        // Create a new exam attempt
        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setStudent(student);
        attempt.setStartTime(now);
        attempt.setIsCompleted(false);

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);

        // Return initial exam data
        Map<String, Object> result = new HashMap<>();
        result.put("attemptId", savedAttempt.getId());
        result.put("examId", exam.getId());
        result.put("title", exam.getTitle());
        result.put("startTime", savedAttempt.getStartTime());
        result.put("durationMinutes", exam.getDurationMinutes());
        result.put("endTime", savedAttempt.getStartTime().plusMinutes(exam.getDurationMinutes()));
        result.put("currentSection", Section.SectionType.APTITUDE.name());

        return result;
    }

    public Map<String, Object> getSectionQuestions(Long attemptId, String sectionType) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        // Verify this attempt belongs to the requesting student
        if (!attempt.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("You do not have access to this exam attempt");
        }

        // Check if exam is still in progress
        if (attempt.getIsCompleted() ||
            attempt.getStartTime().plusMinutes(attempt.getExam().getDurationMinutes()).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Exam has already ended");
        }

        // Get the section
        Section.SectionType type = Section.SectionType.valueOf(sectionType);
        Section section = sectionRepository.findByExamAndSectionType(attempt.getExam(), type)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        // Get questions for the section
        List<Question> questions = questionRepository.findBySection(section);

        // Convert to response objects (removing correct answer information)
        List<QuestionResponse> questionResponses = questions.stream()
                .map(this::convertToQuestionResponse)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("sectionType", section.getSectionType().name());
        result.put("totalQuestions", questionResponses.size());
        result.put("questions", questionResponses);

        return result;
    }

    @Transactional
    public Map<String, Object> submitSectionAnswers(Long attemptId, String sectionType,
                                                   AnswerSubmissionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        // Verify this attempt belongs to the requesting student
        if (!attempt.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("You do not have access to this exam attempt");
        }

        // Verify exam is not completed
        if (attempt.getIsCompleted()) {
            throw new IllegalStateException("Exam is already completed");
        }

        // Save answers (implementation details omitted for brevity)
        // This would involve creating StudentAnswer objects and saving them

        // Determine next section
        Section.SectionType currentType = Section.SectionType.valueOf(sectionType);
        String nextSection = null;

        if (currentType == Section.SectionType.APTITUDE) {
            nextSection = Section.SectionType.REASONING.name();
        } else if (currentType == Section.SectionType.REASONING) {
            nextSection = Section.SectionType.CODING.name();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sectionType", sectionType);
        result.put("submitted", true);
        result.put("nextSection", nextSection);

        List<String> remainingSections = new ArrayList<>();
        if (currentType == Section.SectionType.APTITUDE) {
            remainingSections.add(Section.SectionType.REASONING.name());
            remainingSections.add(Section.SectionType.CODING.name());
        } else if (currentType == Section.SectionType.REASONING) {
            remainingSections.add(Section.SectionType.CODING.name());
        }

        result.put("remainingSections", remainingSections);

        return result;
    }

    @Transactional
    public Map<String, Object> completeExam(Long attemptId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        // Verify this attempt belongs to the requesting student
        if (!attempt.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("You do not have access to this exam attempt");
        }

        // Verify exam is not already completed
        if (attempt.getIsCompleted()) {
            throw new IllegalStateException("Exam is already completed");
        }

        // Mark exam as completed
        attempt.setIsCompleted(true);
        attempt.setEndTime(LocalDateTime.now());
        examAttemptRepository.save(attempt);

        Map<String, Object> result = new HashMap<>();
        result.put("examId", attempt.getExam().getId());
        result.put("attemptId", attempt.getId());
        result.put("completionTime", attempt.getEndTime());
        result.put("submitted", true);
        result.put("message", "Exam completed successfully");

        return result;
    }

    public Map<String, Object> recordFocusLossEvent(Long attemptId, Map<String, Object> eventDetails) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        // Verify this attempt belongs to the requesting student
        if (!attempt.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("You do not have access to this exam attempt");
        }

        // Record focus loss event
        FocusLossEvent event = new FocusLossEvent();
        event.setAttempt(attempt);
        event.setEventTime(LocalDateTime.now());
        event.setEventType((String) eventDetails.get("eventType"));

        if (eventDetails.containsKey("durationSeconds")) {
            event.setDurationSeconds((Integer) eventDetails.get("durationSeconds"));
        }

        // In a real implementation, you would save this event
        // focusLossEventRepository.save(event);

        Map<String, Object> result = new HashMap<>();
        result.put("recorded", true);
        result.put("warningLevel", "MEDIUM");
        result.put("message", "Focus loss event recorded");

        return result;
    }

    private ExamResponse convertToExamResponse(Exam exam) {
        List<ExamResponse.SectionResponse> sectionResponses = exam.getSections().stream()
                .map(section -> ExamResponse.SectionResponse.builder()
                        .id(section.getId())
                        .sectionType(section.getSectionType().name())
                        .title(section.getTitle())
                        .passingMarks(section.getPassingMarks())
                        .build())
                .collect(Collectors.toList());

        return ExamResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .durationMinutes(exam.getDurationMinutes())
                .isActive(exam.getIsActive())
                .sections(sectionResponses)
                .build();
    }

    private QuestionResponse convertToQuestionResponse(Question question) {
        List<QuestionResponse.QuestionOptionResponse> optionResponses = question.getOptions().stream()
                .map(option -> QuestionResponse.QuestionOptionResponse.builder()
                        .id(option.getId())
                        .optionText(option.getOptionText())
                        .build())
                .collect(Collectors.toList());

        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType().name())
                .options(optionResponses)
                .build();
    }
}
