package com.ILPex.controller;

import com.ILPex.DTO.AssessmentCreationDTO;
import com.ILPex.service.AssessmentCreation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/assessments")
public class AssessmentCreationController {

    @Autowired
    private AssessmentCreation assessmentCreationService;

    @PostMapping("/create")
    public ResponseEntity<String> createAssessment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("batchId") Long batchId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        try {
            AssessmentCreationDTO assessmentCreationDTO = assessmentCreationService.parseExcelFile(file, title, batchId, startDate, endDate);
            assessmentCreationService.createAssessment(assessmentCreationDTO);
            return ResponseEntity.ok("Assessment created and notifications sent to all trainees of the batch.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
