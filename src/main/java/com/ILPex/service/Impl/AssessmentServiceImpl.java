package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.Assessments;
import com.ILPex.entity.Questions;
import com.ILPex.entity.Results;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.AssessmentsRepository;
import com.ILPex.repository.ResultsRepository;
import com.ILPex.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService{

    @Autowired
    private AssessmentsRepository assessmentRepository;


    @Autowired
    private ResultsRepository resultsRepository;



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

    // In AssessmentServiceImpl.java
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

}
