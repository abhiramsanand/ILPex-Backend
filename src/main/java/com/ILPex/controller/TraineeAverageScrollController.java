package com.ILPex.controller;

import com.ILPex.DTO.TraineeAverageScoreDTO;
import com.ILPex.service.TraineeAverageScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/trainees")
public class TraineeAverageScrollController {
    @Autowired
    private TraineeAverageScoreService traineeAverageScoreService;

    @GetMapping("/{traineeId}/average-score")
    public ResponseEntity<TraineeAverageScoreDTO> getAverageScoreByTraineeId(@PathVariable Long traineeId) {
        Optional<TraineeAverageScoreDTO> averageScore = traineeAverageScoreService.getAverageScoreByTraineeId(traineeId);
        return averageScore.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
