package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.Assessments;
import com.ILPex.entity.Questions;
import com.ILPex.entity.Results;
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


    @Override
    public Page<AssessmentReportDTO> getAssessmentDetailsByBatchIdAndStatus(Long batchId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("assessmentName").ascending());
        Page<Object[]> resultPage = assessmentRepository.getAssessmentDetailsByBatchIdAndStatus(batchId, status, pageable);

        return resultPage.map(result -> new AssessmentReportDTO(
                (String) result[0], // assessmentName
                (Boolean) result[1], // assessmentStatus
                (String) result[2], // batchName
                (Long) result[3], // numberOfStudentsAttended
                (String) result[4], // traineeName
                (String) result[5], // traineeStatus
                (Integer) result[6] // score
        ));
    }

    @Autowired
    private ResultsRepository resultsRepository;

    public List<TraineeAssessmentDTO> getAssessmentsByTraineeId(int traineeId) {
        return new ArrayList<>();

    }//        return resultsRepository.findAssessmentsByTraineeId(traineeId);


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
    public boolean submitAssessment(TraineeAssessmentSubmissionDTO submissionDTO) {
        Optional<Assessments> assessmentOpt = assessmentRepository.findById(submissionDTO.getAssessmentId());

        if (!assessmentOpt.isPresent()) {
            return false;
        }

        Assessments assessment = assessmentOpt.get();
        int score = 0;

        List<String> submittedResponses = submissionDTO.getResponses();
        List<Questions> questions = new ArrayList<>(assessment.getQuestions());

        if (submittedResponses == null || submittedResponses.size() != questions.size()) {
            return false; // Handle invalid response size
        }

        for (int i = 0; i < questions.size(); i++) {
            Questions question = questions.get(i);
            if (question.getCorrectAnswer().equalsIgnoreCase(submittedResponses.get(i))) {
                score++;
            }
        }

        // Fetch existing result if it exists, otherwise create a new one
        Results result = resultsRepository.findByAssessmentBatchesAllocationIdAndTraineeId(
                submissionDTO.getAssessmentBatchAllocationId(), submissionDTO.getTraineeId()
        ).orElse(new Results());

        result.setAssessmentBatchesAllocationId(submissionDTO.getAssessmentBatchAllocationId());
        result.setTraineeId(submissionDTO.getTraineeId());
        result.setScore(score);
        result.setAssessmentAttempts(result.getAssessmentAttempts() + 1);

        resultsRepository.save(result);

        return true;
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





