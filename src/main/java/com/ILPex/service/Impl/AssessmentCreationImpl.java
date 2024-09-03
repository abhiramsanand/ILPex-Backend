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
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row != null) {
                    try {
                        QuestionCreationDTO question = new QuestionCreationDTO();
                        question.setQuestion(row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "");
                        question.setOptionA(row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "");
                        question.setOptionB(row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "");
                        question.setOptionC(row.getCell(3) != null ? row.getCell(3).getStringCellValue() : "");
                        question.setOptionD(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "");
                        question.setCorrectAnswer(row.getCell(5) != null ? row.getCell(5).getStringCellValue() : "");
                        questionList.add(question);
                    } catch (Exception e) {
                        System.err.println("Error processing row " + i + ": " + e.getMessage());
                    }
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

}
