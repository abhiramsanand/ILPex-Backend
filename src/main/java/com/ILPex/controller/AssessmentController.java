package com.ILPex.controller;


import com.ILPex.DTO.AssessmentReportDTO;
import com.ILPex.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
}
