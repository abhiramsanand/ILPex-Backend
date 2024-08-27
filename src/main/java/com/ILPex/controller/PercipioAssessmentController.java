package com.ILPex.controller;

import com.ILPex.service.PercipioAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/assessments")
public class PercipioAssessmentController {

    @Autowired
    private PercipioAssessmentService percipioAssessmentService;

    @GetMapping("/average")
    public ResponseEntity<Map<Long, Double>> getAverageScoresForAllTrainees() {
        Map<Long, Double> response = percipioAssessmentService.getAverageScoresForAllTrainees();
        return ResponseEntity.ok(response);
    }
@GetMapping("/averageScore")
public ResponseEntity<Map<String, Double>> getAverageScoresForAllTraineesWithName() {
    Map<String, Double> response = percipioAssessmentService.getAverageScoresForAllTraineesWithName();
    return ResponseEntity.ok(response);
}

}
