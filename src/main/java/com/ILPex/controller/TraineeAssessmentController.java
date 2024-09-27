package com.ILPex.controller;


import com.ILPex.DTO.*;
import com.ILPex.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/assessments")
@CrossOrigin(origins = "http://localhost:5173")
public class TraineeAssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @GetMapping("/{id}")
    public ResponseEntity<TraineeAssessmentDisplayDTO> getAssessment(@PathVariable("id") Long assessmentId) {
        TraineeAssessmentDisplayDTO assessmentDTO = assessmentService.getAssessmentById(assessmentId);
        return ResponseEntity.ok(assessmentDTO);
    }

    @GetMapping("/completed/{traineeId}")
    public ResponseEntity<List<TraineeCompletedAssessmentDTO>> getCompletedAssessmentsByTraineeId(@PathVariable int traineeId) {
        List<TraineeCompletedAssessmentDTO> completedAssessments = assessmentService.getCompletedAssessmentsByTraineeId(traineeId);
        return ResponseEntity.ok(completedAssessments);
    }

    @GetMapping("/pending/{traineeId}")
    public ResponseEntity<List<TraineePendingAssessmentDTO>> getPendingAssessments(
            @PathVariable int traineeId) {
        List<TraineePendingAssessmentDTO> pendingAssessments = assessmentService.getPendingAssessmentsByTraineeId(traineeId);
        return ResponseEntity.ok(pendingAssessments);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TraineeAssessmentDisplayDTO> getAssessmentByName(@PathVariable("name") String assessmentName) {
        TraineeAssessmentDisplayDTO assessmentDTO = assessmentService.getAssessmentByName(assessmentName);
        return ResponseEntity.ok(assessmentDTO);
    }

    @PostMapping("/submit")
    public ResponseEntity<AssessmentResultDTO> submitAssessment(@RequestBody AssessmentResponseDTO responseDTO) {
        AssessmentResultDTO result = assessmentService.calculateAssessmentScore(responseDTO);
        return ResponseEntity.ok(result);
    }
}
