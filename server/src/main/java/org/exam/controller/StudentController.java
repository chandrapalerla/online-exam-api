package org.exam.controller;

import jakarta.validation.Valid;
import org.exam.dto.request.AnswerSubmissionRequest;
import org.exam.dto.response.ExamResponse;
import org.exam.dto.response.QuestionResponse;
import org.exam.service.ExamService;
import org.exam.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ExamService examService;

    @GetMapping("/exams")
    public ResponseEntity<Map<String, List<ExamResponse>>> getAvailableExams() {
        return ResponseEntity.ok(studentService.getAvailableExams());
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<Map<String, Object>> startExam(@PathVariable Long examId) {
        return ResponseEntity.ok(studentService.startExam(examId));
    }

    @GetMapping("/attempts/{attemptId}/sections/{sectionType}/questions")
    public ResponseEntity<Map<String, Object>> getSectionQuestions(
            @PathVariable Long attemptId,
            @PathVariable String sectionType) {
        return ResponseEntity.ok(studentService.getSectionQuestions(attemptId, sectionType));
    }

    @PostMapping("/attempts/{attemptId}/sections/{sectionType}/submit")
    public ResponseEntity<Map<String, Object>> submitSectionAnswers(
            @PathVariable Long attemptId,
            @PathVariable String sectionType,
            @Valid @RequestBody AnswerSubmissionRequest request) {
        return ResponseEntity.ok(studentService.submitSectionAnswers(attemptId, sectionType, request));
    }

    @PostMapping("/attempts/{attemptId}/complete")
    public ResponseEntity<Map<String, Object>> completeExam(@PathVariable Long attemptId) {
        return ResponseEntity.ok(studentService.completeExam(attemptId));
    }

    @PostMapping("/attempts/{attemptId}/events/focus-loss")
    public ResponseEntity<Map<String, Object>> logFocusLossEvent(
            @PathVariable Long attemptId,
            @RequestBody Map<String, Object> eventDetails) {
        return ResponseEntity.ok(studentService.recordFocusLossEvent(attemptId, eventDetails));
    }
}
