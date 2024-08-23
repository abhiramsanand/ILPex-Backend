package com.ILPex.controller;

import com.ILPex.DTO.AssessmentCreationDTO;
import com.ILPex.service.AssessmentCreation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/assessmentcreation")
public class AssessmentCreationController {
    @Autowired
    private AssessmentCreation assessmentCreation;

    @PostMapping("/create")
    public ResponseEntity<String> createAssessment(@RequestParam("title") String title,
                                                   @RequestParam("batchId") Long batchId,
                                                   @RequestParam("startDate") String startDate,
                                                   @RequestParam("endDate") String endDate,
                                                   @RequestParam("file") MultipartFile file) {
        try {
            // Convert file to DTO and pass it to service
            AssessmentCreationDTO assessmentCreationDTO = assessmentCreation.parseExcelFile(file, title, batchId, startDate, endDate);
            assessmentCreation.createAssessment(assessmentCreationDTO);
            return new ResponseEntity<>("Assessment created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating assessment: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
