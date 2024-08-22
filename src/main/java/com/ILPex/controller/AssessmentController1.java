package com.ILPex.controller;


import com.ILPex.DTO.TraineeAssessmentDTO;
import com.ILPex.constants.Message;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.TraineeAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ilpex/trainee-assessments")
public class AssessmentController1 {

    private final TraineeAssessmentService traineeAssessmentService;

    @Autowired
    public AssessmentController1(TraineeAssessmentService traineeAssessmentService) {
        this.traineeAssessmentService = traineeAssessmentService;
    }

    @GetMapping
    public ResponseEntity<Object> getTraineeAssessmentDetails() {
        List<TraineeAssessmentDTO> traineeAssessmentDetails = traineeAssessmentService.getTraineeAssessmentDetails();
        return ResponseHandler.responseBuilder(
                Message.ASSESSMENT_DETAILS_RETRIEVED,
                HttpStatus.OK,
                traineeAssessmentDetails
        );
    }
}