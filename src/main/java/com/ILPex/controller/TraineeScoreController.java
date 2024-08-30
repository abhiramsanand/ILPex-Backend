package com.ILPex.controller;

import com.ILPex.service.TraineeScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/trainee-scores")
public class TraineeScoreController {

    @Autowired
    private TraineeScoreService traineeScoreService;

    @GetMapping("/average/name")
    public ResponseEntity<Map<String, Double>> getAverageScoresForAllTraineesByName() {
        Map<String, Double> averageScoresByName = traineeScoreService.getAverageScoresForAllTraineesByName();
        return ResponseEntity.ok(averageScoresByName);
    }
}
