package org.exam.service;

import org.exam.dto.response.ExamResponse;
import org.exam.dto.response.QuestionResponse;
import org.exam.exception.ResourceNotFoundException;
import org.exam.model.Exam;
import org.exam.model.Question;
import org.exam.model.Section;
import org.exam.repository.ExamRepository;
import org.exam.repository.QuestionRepository;
import org.exam.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for exam-related operations that are common for both students and admins
 */
@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Get an exam by its ID
     *
     * @param examId the ID of the exam
     * @return the exam response
     * @throws ResourceNotFoundException if exam not found
     */
    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        return convertToExamResponse(exam);
    }

    /**
     * Get all available exams
     *
     * @return list of exam responses
     */
    @Transactional(readOnly = true)
    public List<ExamResponse> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        return exams.stream().map(this::convertToExamResponse).collect(Collectors.toList());
    }

    /**
     * Get all questions for a section
     *
     * @param examId the ID of the exam
     * @param sectionId the ID of the section
     * @return list of question responses
     */
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsBySection(Long examId, Long sectionId) {
        // Verify exam exists
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        // Verify section exists and belongs to this exam
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        if (!section.getExam().getId().equals(examId)) {
            throw new ResourceNotFoundException("Section does not belong to the specified exam");
        }

        List<Question> questions = questionRepository.findBySection(section);
        return questions.stream().map(this::convertToQuestionResponse).collect(Collectors.toList());
    }

    /**
     * Helper method to convert Exam entity to ExamResponse DTO
     */
    private ExamResponse convertToExamResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        response.setId(exam.getId());
        response.setTitle(exam.getTitle());
        response.setDescription(exam.getDescription());
        response.setStartTime(exam.getStartTime());
        response.setEndTime(exam.getEndTime());
        response.setDurationMinutes(exam.getDurationMinutes());
        response.setIsActive(exam.getIsActive());

        // Map section information
        if (exam.getSections() != null) {
            response.setSections(exam.getSections().stream().map(section -> {
                ExamResponse.SectionResponse sectionResponse = new ExamResponse.SectionResponse();
                sectionResponse.setId(section.getId());
                sectionResponse.setTitle(section.getTitle());
                sectionResponse.setSectionType(section.getSectionType().name());
                sectionResponse.setPassingMarks(section.getPassingMarks());
                return sectionResponse;
            }).collect(Collectors.toList()));
        }

        return response;
    }

    /**
     * Helper method to convert Question entity to QuestionResponse DTO
     */
    private QuestionResponse convertToQuestionResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestionText(question.getQuestionText());
        response.setQuestionType(question.getQuestionType().name());

        // Map options if available
        if (question.getOptions() != null) {
            response.setOptions(question.getOptions().stream().map(option -> {
                QuestionResponse.QuestionOptionResponse optionResponse = new QuestionResponse.QuestionOptionResponse();
                optionResponse.setId(option.getId());
                optionResponse.setOptionText(option.getOptionText());
                return optionResponse;
            }).collect(Collectors.toList()));
        }

        return response;
    }
}
