package com.ILPex.controller;



import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeDailyReportDTO;
import com.ILPex.DTO.TraineeDayProgressDTO;
import com.ILPex.DTO.TraineeProfileDTO;
import com.ILPex.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
@CrossOrigin(origins = "http://localhost:5173")
public class TraineeController {

    @Autowired
    private TraineeService traineeService;

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<TraineeDTO>> getTraineesByBatchId(@PathVariable Long batchId) {
        List<TraineeDTO> trainees = traineeService.getTraineesByBatchId(batchId);
        return ResponseEntity.ok(trainees);
    }

    @GetMapping("/reports")
    public ResponseEntity<List<TraineeDailyReportDTO>> getTraineeReportsByBatchId(@RequestParam Long batchId) {
        List<TraineeDailyReportDTO> traineeReports = traineeService.getTraineeReportsByBatchId(batchId);
        return ResponseEntity.ok(traineeReports);
    }

    @GetMapping("/{traineeId}/currentdaynumber")
    public Long getCurrentBatchDayNumber(@PathVariable Long traineeId) {
        return traineeService.getCurrentBatchDayNumber(traineeId);
    } 
    
    @GetMapping("/batch/{batchId}/currentday")
    public List<TraineeDayProgressDTO> getLastDayForTraineesInBatch(@PathVariable Long batchId) {
        return  traineeService.getLastDayForTrainees(batchId);
    }

    @GetMapping("/{traineeId}")
    public ResponseEntity<TraineeProfileDTO> getTraineeProfile(@PathVariable Long traineeId) {
        TraineeProfileDTO profile = traineeService.getTraineeProfile(traineeId);
        return ResponseEntity.ok(profile);
    }
}