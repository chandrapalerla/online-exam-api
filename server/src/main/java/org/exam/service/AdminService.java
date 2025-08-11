package org.exam.service;

import org.exam.dto.request.ExamCreationRequest;
import org.exam.dto.request.QuestionCreationRequest;
import org.exam.dto.request.ReportGenerationRequest;
import org.exam.dto.response.ExamResponse;
import org.exam.exception.ResourceNotFoundException;
import org.exam.model.*;
import org.exam.repository.*;
import org.exam.util.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private ExamReportRepository examReportRepository;

    @Autowired
    private PdfGenerator pdfGenerator;

    @Transactional
    public ExamResponse createExam(ExamCreationRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        // Create the exam
        Exam exam = new Exam();
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setIsActive(false);
        exam.setCreatedBy(admin);

        Exam savedExam = examRepository.save(exam);

        // Create sections
        List<Section> sections = new ArrayList<>();
        for (ExamCreationRequest.SectionRequest sectionRequest : request.getSections()) {
            Section section = new Section();
            section.setExam(savedExam);
            section.setSectionType(Section.SectionType.valueOf(sectionRequest.getSectionType()));
            section.setTitle(sectionRequest.getTitle());
            section.setDescription(sectionRequest.getDescription());
            section.setPassingMarks(sectionRequest.getPassingMarks());

            sections.add(section);
        }

        List<Section> savedSections = sectionRepository.saveAll(sections);
        savedExam.setSections(savedSections);

        // Convert to response
        return convertToExamResponse(savedExam);
    }

    @Transactional
    public Map<String, Object> addQuestionsToSection(Long examId, Long sectionId,
                                                    List<QuestionCreationRequest> questionsRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        // Verify section belongs to the exam
        if (!section.getExam().getId().equals(exam.getId())) {
            throw new IllegalStateException("Section does not belong to the specified exam");
        }

        // Create questions
        List<Question> questions = new ArrayList<>();
        for (QuestionCreationRequest questionRequest : questionsRequest) {
            Question question = new Question();
            question.setSection(section);
            question.setQuestionText(questionRequest.getQuestionText());
            question.setQuestionType(Question.QuestionType.valueOf(questionRequest.getQuestionType()));
            question.setMarks(questionRequest.getMarks());

            Question savedQuestion = questionRepository.save(question);

            // Create options if applicable
            if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
                List<QuestionOption> options = new ArrayList<>();

                for (QuestionCreationRequest.QuestionOptionRequest optionRequest : questionRequest.getOptions()) {
                    QuestionOption option = new QuestionOption();
                    option.setQuestion(savedQuestion);
                    option.setOptionText(optionRequest.getOptionText());
                    option.setIsCorrect(optionRequest.getIsCorrect());

                    options.add(option);
                }

                List<QuestionOption> savedOptions = questionOptionRepository.saveAll(options);
                savedQuestion.setOptions(savedOptions);
            }

            questions.add(savedQuestion);
        }

        // Return summary
        Map<String, Object> result = new HashMap<>();
        result.put("sectionId", sectionId);
        result.put("questionsAdded", questions.size());
        result.put("totalQuestions", questionRepository.countBySection(section));

        return result;
    }

    @Transactional
    public Map<String, Object> generateReport(ReportGenerationRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // Create report record
        ExamReport report = new ExamReport();
        report.setExam(exam);
        report.setGeneratedBy(admin);
        report.setCollege(request.getCollege());
        report.setAptitudePassingMarks(request.getPassingCriteria().getAptitude());
        report.setReasoningPassingMarks(request.getPassingCriteria().getReasoning());
        report.setCodingPassingMarks(request.getPassingCriteria().getCoding());

        ExamReport savedReport = examReportRepository.save(report);

        // Generate PDF (in a real implementation, this might be done asynchronously)
        // For simplicity, we're just setting a placeholder path
        String reportPath = "reports/" + savedReport.getId() + ".pdf";
        savedReport.setReportPath(reportPath);
        examReportRepository.save(savedReport);

        // Here we would actually generate the PDF using the PdfGenerator service
        // pdfGenerator.generateExamReport(savedReport);

        // Return report details
        Map<String, Object> result = new HashMap<>();
        result.put("reportId", savedReport.getId());
        result.put("examId", exam.getId());
        result.put("generatedAt", savedReport.getGeneratedAt());
        result.put("reportUrl", "/api/admin/reports/" + savedReport.getId() + "/download");

        // In a real implementation, we would compute these statistics
        result.put("totalStudents", 50);
        result.put("passedStudents", 35);
        result.put("failedStudents", 15);

        return result;
    }

    public Resource getReportResource(Long reportId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        ExamReport report = examReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        // In a real implementation, we would fetch the PDF file
        // For now, we'll just return a placeholder
        String dummyPdf = "This is a PDF report placeholder";
        return new ByteArrayResource(dummyPdf.getBytes());
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
}
