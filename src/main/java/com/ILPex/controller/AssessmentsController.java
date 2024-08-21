package com.ILPex.controller;


import com.ILPex.DTO.AssessmentDetailsDTO;
import com.ILPex.constants.Message;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.AssessmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ilpex/assessments")
public class AssessmentsController {

    private final AssessmentsService assessmentsService;

    @Autowired
    public AssessmentsController(AssessmentsService assessmentsService) {
        this.assessmentsService = assessmentsService;
    }

    @GetMapping("/details")
    public ResponseEntity<Object> getAssessmentDetails(@RequestParam Long batchId) {
        List<AssessmentDetailsDTO> assessmentDetails = assessmentsService.getAssessmentDetailsByBatchId(batchId);
        return ResponseHandler.responseBuilder(
                Message.ASSESSMENT_DETAILS_RETRIEVED, HttpStatus.OK, assessmentDetails
        );
    }
}