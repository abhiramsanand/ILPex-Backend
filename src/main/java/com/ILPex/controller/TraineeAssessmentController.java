package com.ILPex.controller;


import com.ILPex.DTO.TraineeAssessmentDTO;
import com.ILPex.service.TraineeAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainee-assessments")
public class TraineeAssessmentController {

    private final TraineeAssessmentService traineeAssessmentService;

    @Autowired
    public TraineeAssessmentController(TraineeAssessmentService traineeAssessmentService) {
        this.traineeAssessmentService = traineeAssessmentService;
    }

    @GetMapping
    public List<TraineeAssessmentDTO> getTraineeAssessmentDetails() {
        return traineeAssessmentService.getTraineeAssessmentDetails();
    }
}