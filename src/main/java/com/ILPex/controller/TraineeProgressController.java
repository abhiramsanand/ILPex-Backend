package com.ILPex.controller;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.DTO.TraineeActualVsEstimatedDurationDTO;
import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeDurationDTO;
import com.ILPex.constants.Message;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.TraineeProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/ilpex/traineeprogress")
public class TraineeProgressController {

    @Autowired
    private TraineeProgressService traineeProgressService;

    @GetMapping("/status")
    public Map<String, Integer> getProgressStatusCounts() {
        return traineeProgressService.getProgressStatusCounts();
    }

    @GetMapping("/last-accessed-day-number")
    public ResponseEntity<Map<Long, Integer>> getLastAccessedDayNumberForTrainees() {
        Map<Long, Integer> traineeDayNumberMap = traineeProgressService.getLastAccessedDayNumberForTrainees();
        return ResponseEntity.ok(traineeDayNumberMap);
    }

    @GetMapping("/trainee/last-accessed-day-number")
    public ResponseEntity<Map<String, Integer>> getLastAccessedDayNumberForTraineesName() {
        Map<String, Integer> traineeDayNumberMap = traineeProgressService.getLastAccessedDayNumberForTraineesName();
        return ResponseEntity.ok(traineeDayNumberMap);
    }

    @GetMapping("/Daily-Report-day-number")
    public Map<Long, Integer> getDayNumberForTrainees() {
        return traineeProgressService.getDayNumberForTrainees();
    }

    @GetMapping("/{traineeId}")
    public List<CourseProgressDTO> getTraineeProgress(@PathVariable Long traineeId) {
        return traineeProgressService.getTraineeProgress(traineeId);
    }
    @GetMapping("/duration")
    public ResponseEntity<List<TraineeActualVsEstimatedDurationDTO>> getTotalDurationAndEstimatedDurationByTraineeAndBatch(
            @RequestParam Long batchId) {
        List<TraineeActualVsEstimatedDurationDTO> results = traineeProgressService.getTotalDurationAndEstimatedDurationByTraineeIdAndBatch(batchId);
        return ResponseEntity.ok(results);
    }
}