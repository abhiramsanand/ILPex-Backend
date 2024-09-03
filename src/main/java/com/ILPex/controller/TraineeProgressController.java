package com.ILPex.controller;

import com.ILPex.DTO.*;
import com.ILPex.constants.Message;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.TraineeProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/ilpex/traineeprogress")
public class TraineeProgressController {

    @Autowired
    private TraineeProgressService traineeProgressService;

    @GetMapping("/trainee/last-accessed-day-number")
    public ResponseEntity<List<Map<String, Object>>> getLastAccessedDayNumberForTrainees() {
        List<Map<String, Object>> traineeList = traineeProgressService.getLastAccessedDayNumberForTrainees();
        return ResponseEntity.ok(traineeList);
    }

    @GetMapping("/{traineeId}")
    public List<CourseProgressDTO> getTraineeProgress(@PathVariable Long traineeId) {
        return traineeProgressService.getTraineeProgress(traineeId);
    }

    @GetMapping("/course-duration")
    public ResponseEntity<List<TraineeCourseDurationDTO>> getTotalCourseDurationByBatchId(
            @RequestParam Long batchId) {
        List<TraineeCourseDurationDTO> response = traineeProgressService.findTotalCourseDurationDTOByBatchId(batchId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course-count/{batchId}")
    public List<TraineeCourseCountDTO> getDistinctCourseDurationCountByBatchId(@PathVariable Long batchId) {
        return traineeProgressService.getDistinctCourseDurationCountByBatchId(batchId);
    }

    @GetMapping("/duration")
    public ResponseEntity<List<TraineeActualVsEstimatedDurationDTO>> getTotalDurationAndEstimatedDurationByTraineeAndBatch(
            @RequestParam Long batchId) {
        List<TraineeActualVsEstimatedDurationDTO> results = traineeProgressService.getTotalDurationAndEstimatedDurationByTraineeIdAndBatch(batchId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/currentdaynumber/{traineeId}")
    public TraineeCurrentDayDTO getMaxDayNumber(@PathVariable Long traineeId) {
        return traineeProgressService.getMaxDayNumber(traineeId);
    }
    
    @GetMapping("/actualVsEstimateDuration")
    public List<TraineeProgressDTO> getTraineeProgress(@RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") Date courseDate, @RequestParam Long traineeId) {
        Timestamp courseDateTimestamp = new Timestamp(courseDate.getTime());
        return traineeProgressService.getTraineeProgressByCourseDateAndTraineeId(courseDateTimestamp, traineeId);
    }
}