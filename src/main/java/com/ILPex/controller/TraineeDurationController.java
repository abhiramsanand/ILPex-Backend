package com.ILPex.controller;

import com.ILPex.service.TraineeDurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/accelerated")
@CrossOrigin(origins = "http://localhost:5173")
public class TraineeDurationController {

    @Autowired
    private TraineeDurationService traineeDurationService;

    @GetMapping
    public Map<String, Object> getAcceleratedPercentage(@RequestParam Long batchId) {
        double percentage = traineeDurationService.calculateAcceleratedPercentage(batchId);
        return Map.of("batchId", batchId, "percentage", percentage);
    }
}
