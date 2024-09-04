package com.ILPex.service.Impl;

import com.ILPex.DTO.AssessmentCreationDTO;
import com.ILPex.DTO.QuestionCreationDTO;
import com.ILPex.entity.Assessments;
import com.ILPex.entity.AssessmentBatchAllocation;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Questions;
import com.ILPex.repository.AssessmentBatchAllocationRepository;
import com.ILPex.repository.AssessmentsRepository;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.QuestionsRepository;
import com.ILPex.service.AssessmentCreation;
import com.ILPex.service.AssessmentNotificationService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssessmentCreationImpl implements AssessmentCreation {

    @Autowired
    private AssessmentsRepository assessmentRepository;

    @Autowired
    private AssessmentBatchAllocationRepository allocationRepository;

    @Autowired
    private QuestionsRepository questionRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private AssessmentNotificationService assessmentNotificationService;

    @Override
    public void createAssessment(AssessmentCreationDTO assessmentCreationDTO) throws Exception {
        // Create and save the assessment
        Assessments assessment = new Assessments();
        assessment.setAssessmentName(assessmentCreationDTO.getTitle());
        Timestamp endDate = Timestamp.valueOf(assessmentCreationDTO.getEndDate() + " 23:59:59");
        assessment.setDueDate(endDate);

        // Calculate is_active based on endDate
        boolean isActive = endDate.after(new Timestamp(System.currentTimeMillis()));
        assessment.setIsActive(isActive);

        // Save the assessment
        assessment = assessmentRepository.save(assessment);

        // Create and save the batch allocation
        Optional<Batches> batchOptional = batchRepository.findById(assessmentCreationDTO.getBatchId());
        if (!batchOptional.isPresent()) {
            throw new Exception("Batch not found");
        }
        Batches batch = batchOptional.get();

        AssessmentBatchAllocation allocation = new AssessmentBatchAllocation();
        allocation.setAssessments(assessment);
        allocation.setBatches(batch);
        allocation.setStartDate(Timestamp.valueOf(assessmentCreationDTO.getStartDate() + " 00:00:00"));
        allocation.setEndDate(endDate);
        allocation.setAssessmentStatus(true);
        allocationRepository.save(allocation);

        // Save questions
        List<QuestionCreationDTO> questionDTOs = assessmentCreationDTO.getQuestions();
        for (QuestionCreationDTO questionDTO : questionDTOs) {
            Questions question = new Questions();
            question.setQuestion(questionDTO.getQuestion());
            question.setOptionA(questionDTO.getOptionA());
            question.setOptionB(questionDTO.getOptionB());
            question.setOptionC(questionDTO.getOptionC());
            question.setOptionD(questionDTO.getOptionD());
            question.setCorrectAnswer(questionDTO.getCorrectAnswer());
            question.setAssessments(assessment);
            questionRepository.save(question);
        }


        // Notify all trainees of the batch
        assessmentNotificationService.notifyAllTraineesOfBatch(batch.getId(), allocation.getId());
    }


    @Override
    public AssessmentCreationDTO parseExcelFile(MultipartFile file, String title, Long batchId, String startDate, String endDate) throws Exception {
        List<QuestionCreationDTO> questionList = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum(); // Get the last row number with content

            for (int i = 1; i <= lastRowNum; i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row != null && isRowNotEmpty(row)) { // Check if the row is not empty
                    QuestionCreationDTO question = new QuestionCreationDTO();
                    question.setQuestion(getCellValue(row, 0));
                    question.setOptionA(getCellValue(row, 1));
                    question.setOptionB(getCellValue(row, 2));
                    question.setOptionC(getCellValue(row, 3));
                    question.setOptionD(getCellValue(row, 4));
                    question.setCorrectAnswer(getCellValue(row, 5));
                    questionList.add(question);
                }
            }
        }

        AssessmentCreationDTO assessmentCreationDTO = new AssessmentCreationDTO();
        assessmentCreationDTO.setTitle(title);
        assessmentCreationDTO.setBatchId(batchId);
        assessmentCreationDTO.setStartDate(startDate);
        assessmentCreationDTO.setEndDate(endDate);
        assessmentCreationDTO.setQuestions(questionList);

        return assessmentCreationDTO;
    }

    // Helper method to get cell value safely
    private String getCellValue(Row row, int cellIndex) {
        if (row.getCell(cellIndex) != null) {
            return row.getCell(cellIndex).getStringCellValue().trim(); // Trim to remove extra spaces
        } else {
            return ""; // or null, depending on how you want to handle missing values
        }
    }

    // Helper method to check if the row is empty
    private boolean isRowNotEmpty(Row row) {
        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            if (row.getCell(cellIndex) != null && !row.getCell(cellIndex).getStringCellValue().trim().isEmpty()) {
                return true; // The row has some content
            }
        }
        return false; // The row is empty
    }


}