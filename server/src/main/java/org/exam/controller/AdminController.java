package org.exam.controller;

import jakarta.validation.Valid;
import org.exam.dto.request.ExamCreationRequest;
import org.exam.dto.request.QuestionCreationRequest;
import org.exam.dto.request.ReportGenerationRequest;
import org.exam.dto.response.ExamResponse;
import org.exam.service.AdminService;
import org.exam.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ExamService examService;

    @PostMapping("/exams")
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamCreationRequest request) {
        return ResponseEntity.ok(adminService.createExam(request));
    }

    @PostMapping("/exams/{examId}/sections/{sectionId}/questions")
    public ResponseEntity<Map<String, Object>> addQuestionsToSection(
            @PathVariable Long examId,
            @PathVariable Long sectionId,
            @Valid @RequestBody List<QuestionCreationRequest> questions) {
        return ResponseEntity.ok(adminService.addQuestionsToSection(examId, sectionId, questions));
    }

    @PostMapping("/reports")
    public ResponseEntity<Map<String, Object>> generateReport(@Valid @RequestBody ReportGenerationRequest request) {
        return ResponseEntity.ok(adminService.generateReport(request));
    }

    @GetMapping("/reports/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long reportId) {
        Resource resource = adminService.getReportResource(reportId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.pdf\"")
                .body(resource);
    }
}
