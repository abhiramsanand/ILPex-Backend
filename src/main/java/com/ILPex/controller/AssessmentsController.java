package com.ILPex.controller;


import com.ILPex.DTO.AssessmentDetailsDTO;
import com.ILPex.service.AssessmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentsController {

    private final AssessmentsService assessmentsService;

    @Autowired
    public AssessmentsController(AssessmentsService assessmentsService) {
        this.assessmentsService = assessmentsService;
    }

    @GetMapping("/details")
    public List<AssessmentDetailsDTO> getAssessmentDetails() {
        return assessmentsService.getAssessmentDetails();
    }
}