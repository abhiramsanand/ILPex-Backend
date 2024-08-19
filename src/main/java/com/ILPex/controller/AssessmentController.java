package com.ILPex.controller;


import com.ILPex.DTO.AssessmentReportDTO;
import com.ILPex.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @GetMapping("/details")
    public List<AssessmentReportDTO> getAssessmentDetails() {
        return  assessmentService.getAssessmentDetails();
    }
}
