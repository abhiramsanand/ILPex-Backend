package com.ILPex.controller;

import com.ILPex.DTO.TraineeAverageScoreDTO;
import com.ILPex.service.ResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/results")
public class ResultsController {

    @Autowired
    private ResultsService resultsService;

    @GetMapping("/average-score/{traineeId}")
    public TraineeAverageScoreDTO getAverageScoreByTraineeId(@PathVariable Long traineeId) {
        return resultsService.getAverageScoreByTraineeId(traineeId);
    }
}