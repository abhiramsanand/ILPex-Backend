package com.ILPex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ILPex.service.TraineeReportStatusService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class TraineeReportStatusController {

    @Autowired
    private TraineeReportStatusService traineeReportStatusService;

    @GetMapping
    public Map<String, Object> getLaggingPercentage(@RequestParam Long batchId) {
        double percentage = traineeReportStatusService.calculateLaggingPercentage(batchId);
        return Map.of("batchId", batchId, "percentage", percentage);
    }
}
