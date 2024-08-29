package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.*;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.*;
import com.ILPex.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService{

    @Autowired
    private AssessmentsRepository assessmentRepository;

    @Autowired
    private ResultsRepository resultsRepository;

    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private TraineesRepository traineesRepository;

    @Autowired
    private AssessmentBatchAllocationRepository assessmentBatchAllocationRepository;

    @Override
    public TraineeAssessmentDisplayDTO getAssessmentById(Long assessmentId) {
        Assessments assessments = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        List<QuestionsDTO> questionDTOList = assessments.getQuestions().stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());

        return new TraineeAssessmentDisplayDTO(assessments.getAssessmentName(), questionDTOList);
    }


    @Override
    public List<TraineeCompletedAssessmentDTO> getCompletedAssessmentsByTraineeId(int traineeId) {
        return resultsRepository.findCompletedAssessmentsByTraineeId(traineeId);
    }

    @Override
    public List<TraineePendingAssessmentDTO> getPendingAssessmentsByTraineeId(int traineeId) {
        return assessmentRepository.findPendingAssessmentsByTraineeId(traineeId);
    }

    @Override
    public TraineeAssessmentDisplayDTO getAssessmentByName(String assessmentName) {
        // Fetch the assessment by name
        Assessments assessment = assessmentRepository.findByAssessmentName(assessmentName);
        if (assessment != null) {
            // Manually map the Assessment entity to DTO
            TraineeAssessmentDisplayDTO dto = new TraineeAssessmentDisplayDTO();
            dto.setAssessmentName(assessment.getAssessmentName());
            // Assuming you have a method to convert Questions entities to QuestionsDTOs
            dto.setQuestions(assessment.getQuestions().stream()
                    .map(question -> new QuestionsDTO(
                            question.getQuestion(),
                            question.getOptionA(),
                            question.getOptionB(),
                            question.getOptionC(),
                            question.getOptionD()
                    ))
                    .collect(Collectors.toList()));
            return dto;
        } else {
            throw new ResourceNotFoundException("Assessment not found with name: " + assessmentName);
        }
    }



    private QuestionsDTO convertToQuestionDTO(Questions question) {
        return new QuestionsDTO(
                question.getQuestion(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD()
        );
    }

    @Override
    public int calculateAssessmentScore(AssessmentResponseDTO responseDTO) {
        // Initialize counters
        int totalQuestions = 0;
        int correctAnswers = 0;

        // Get the question responses from the DTO
        Map<Long, String> questionResponses = responseDTO.getQuestionResponses();

        // Check if the map is null or empty
        if (questionResponses == null || questionResponses.isEmpty()) {
            throw new IllegalArgumentException("Question responses cannot be null or empty");
        }

        // Iterate through each response
        for (Map.Entry<Long, String> entry : questionResponses.entrySet()) {
            Long questionId = entry.getKey();
            String givenAnswer = entry.getValue();

            // Fetch the question from the repository
            Questions question = questionsRepository.findById(questionId).orElse(null);
            if (question != null) {
                totalQuestions++;
                // Compare the given answer with the correct answer
                if (question.getCorrectAnswer().equalsIgnoreCase(givenAnswer)) {
                    correctAnswers++;
                }
            }
        }

        // Calculate the score as a percentage
        int score = totalQuestions > 0 ? (int) ((double) correctAnswers / totalQuestions * 100) : 0;

        // Save the result to the database
        Results result = new Results();
        result.setScore(score);
        result.setAssessmentAttempts(1); // Adjust if multiple attempts are tracked

        // Find and set the assessment batch allocation
        Assessments assessment = assessmentRepository.findById(responseDTO.getAssessmentId()).orElse(null);
        Trainees trainee = traineesRepository.findById(responseDTO.getTraineeId()).orElse(null);
        if (assessment != null && trainee != null) {
            AssessmentBatchAllocation batchAllocation = assessmentBatchAllocationRepository
                    .findByAssessmentsAndBatches(assessment, trainee.getBatches());

            result.setAssessmentBatchAllocation(batchAllocation);
            result.setTrainees(trainee);

            // Save the result to the repository
            resultsRepository.save(result);
        } else {
            throw new IllegalArgumentException("Invalid assessment or trainee ID");
        }

        return score;
    }

}
