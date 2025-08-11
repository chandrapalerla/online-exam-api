package org.exam.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.exam.model.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfGenerator {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    public void generateExamReport(ExamReport report, List<ExamAttempt> attempts) {
        try {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addReportHeader(document, report);
            addExamDetails(document, report.getExam());
            addStudentResults(document, attempts, report);
            document.close();

            // Save to file system
            String filePath = "reports/" + report.getId() + ".pdf";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            outputStream.writeTo(fileOutputStream);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private void addReportHeader(Document document, ExamReport report) throws DocumentException {
        Paragraph title = new Paragraph("Exam Result Report", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(Chunk.NEWLINE);

        Paragraph college = new Paragraph("College: " + report.getCollege(), SUBTITLE_FONT);
        document.add(college);

        Paragraph generated = new Paragraph("Generated on: " +
                report.getGeneratedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), NORMAL_FONT);
        document.add(generated);

        document.add(Chunk.NEWLINE);
    }

    private void addExamDetails(Document document, Exam exam) throws DocumentException {
        Paragraph examTitle = new Paragraph("Exam: " + exam.getTitle(), SUBTITLE_FONT);
        document.add(examTitle);

        if (exam.getDescription() != null && !exam.getDescription().isEmpty()) {
            Paragraph description = new Paragraph("Description: " + exam.getDescription(), NORMAL_FONT);
            document.add(description);
        }

        Paragraph duration = new Paragraph("Duration: " + exam.getDurationMinutes() + " minutes", NORMAL_FONT);
        document.add(duration);

        document.add(Chunk.NEWLINE);
    }

    private void addStudentResults(Document document, List<ExamAttempt> attempts, ExamReport report)
            throws DocumentException {

        Paragraph resultsTitle = new Paragraph("Student Results", SUBTITLE_FONT);
        document.add(resultsTitle);

        document.add(Chunk.NEWLINE);

        // Create results table
        PdfPTable table = new PdfPTable(8); // 8 columns
        table.setWidthPercentage(100);

        // Add table headers
        addTableHeader(table);

        // Add data rows
        for (ExamAttempt attempt : attempts) {
            addStudentResultRow(table, attempt, report);
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Add passing criteria
        Paragraph passCriteria = new Paragraph("Passing Criteria:", SUBTITLE_FONT);
        document.add(passCriteria);

        Paragraph aptitudeCriteria = new Paragraph("Aptitude Section: " + report.getAptitudePassingMarks() + " marks", NORMAL_FONT);
        document.add(aptitudeCriteria);

        Paragraph reasoningCriteria = new Paragraph("Reasoning Section: " + report.getReasoningPassingMarks() + " marks", NORMAL_FONT);
        document.add(reasoningCriteria);

        Paragraph codingCriteria = new Paragraph("Coding Section: " + report.getCodingPassingMarks() + " marks", NORMAL_FONT);
        document.add(codingCriteria);
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"Student ID", "Name", "Branch", "Academic Year",
                           "Aptitude Score", "Reasoning Score", "Coding Score", "Result"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private void addStudentResultRow(PdfPTable table, ExamAttempt attempt, ExamReport report) {
        Student student = attempt.getStudent();

        // In a real implementation, we would calculate these scores from the answers
        int aptitudeScore = 15; // Example score
        int reasoningScore = 12; // Example score
        int codingScore = 14; // Example score

        // Determine if passed
        boolean passedAptitude = aptitudeScore >= report.getAptitudePassingMarks();
        boolean passedReasoning = reasoningScore >= report.getReasoningPassingMarks();
        boolean passedCoding = codingScore >= report.getCodingPassingMarks();
        boolean passed = passedAptitude && passedReasoning && passedCoding;

        // Add cells
        table.addCell(student.getStudentId());
        table.addCell(student.getFullName());
        table.addCell(student.getBranch());
        table.addCell(student.getAcademicYear());
        table.addCell(String.valueOf(aptitudeScore));
        table.addCell(String.valueOf(reasoningScore));
        table.addCell(String.valueOf(codingScore));

        PdfPCell resultCell = new PdfPCell(new Phrase(passed ? "PASS" : "FAIL", NORMAL_FONT));
        resultCell.setBackgroundColor(passed ? new BaseColor(200, 255, 200) : new BaseColor(255, 200, 200));
        resultCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(resultCell);
    }
}
