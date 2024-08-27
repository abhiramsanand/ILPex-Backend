package com.ILPex.controller;



import com.ILPex.DTO.TraineeDTO;
import com.ILPex.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    @Autowired
    private TraineeService traineeService;

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<TraineeDTO>> getTraineesByBatchId(@PathVariable Long batchId) {
        List<TraineeDTO> trainees = traineeService.getTraineesByBatchId(batchId);
        return ResponseEntity.ok(trainees);
    }
}