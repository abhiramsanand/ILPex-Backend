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
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @GetMapping("/batch/{batchId}")
    public Page<AssessmentReportDTO> getAssessmentDetailsByBatchIdAndStatus(
            @PathVariable Long batchId,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return assessmentService.getAssessmentDetailsByBatchIdAndStatus(batchId, status, page, size);
    }



//    @GetMapping("/trainee/{traineeId}")
//    public ResponseEntity<List<TraineeAssessmentDTO>> getAssessmentsByTrainee(@PathVariable int traineeId) {
//        List<TraineeAssessmentDTO> assessments = assessmentService.getAssessmentsByTraineeId(traineeId);
//        return new ResponseEntity<>(assessments, HttpStatus.OK);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<TraineeAssessmentDisplayDTO> getAssessment(@PathVariable("id") Long assessmentId) {
        TraineeAssessmentDisplayDTO assessmentDTO = assessmentService.getAssessmentById(assessmentId);
        return ResponseEntity.ok(assessmentDTO);
    }

//    @PostMapping("/submit")
//    public ResponseEntity<String> submitAssessment(
//            @RequestBody TraineeAssessmentSubmissionDTO submissionDTO) {
//        boolean isSubmitted = assessmentService.submitAssessment(submissionDTO);
//        if (isSubmitted) {
//            return new ResponseEntity<>("Assessment submitted successfully", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Assessment submission failed", HttpStatus.BAD_REQUEST);
//        }
//    }


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

}
