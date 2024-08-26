package com.ILPex.controller;

import com.ILPex.DTO.*;
import com.ILPex.entity.DailyReports;
import com.ILPex.service.DailyReportService;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dailyreport")
public class DailyReportController {
    @Autowired
    private DailyReportService dailyReportService;

    @GetMapping("/courseDetails")       //To view coursename and time taken
    public List<DailyReportDTO> getCourseDetailsWithTimeTaken(
            @RequestParam("courseDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime courseDate,
            @RequestParam("batchId") Long batchId,
            @RequestParam("traineeId") Long traineeId) {

        return dailyReportService.getCourseDetailsWithTimeTaken(courseDate, batchId, traineeId);
    }

    @GetMapping("/learningPlanDetails")       //To view keyLearnings and Plan for tomorrow
    public ResponseEntity<DailyReportDetailsDTO> getLearningPlanDetails(
            @RequestParam("courseId") Long courseId,
            @RequestParam("traineeId") Long traineeId) {

        DailyReportDetailsDTO learningPlan = dailyReportService.getLearningPlanDetails(courseId, traineeId);

        if (learningPlan != null) {
            return ResponseEntity.ok(learningPlan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/editDetails")     //To view details to edit
    public DailyReportEditDTO getDailyReportEditDetails(@RequestParam Long dailyReportId) {
        return dailyReportService.getDailyReportEditDetails(dailyReportId);
    }

        @PostMapping("/updateDetails")  //To post updated report
    public ResponseEntity<DailyReports> addOrUpdateDailyReport(

            @RequestBody DailyReportRequestDTO dailyReportRequestDTO,
            @RequestParam Long traineeId,
            @RequestParam Long courseId) {

        DailyReports updatedReport = dailyReportService.addOrUpdateDailyReport(dailyReportRequestDTO, traineeId, courseId);
        return ResponseEntity.ok(updatedReport);
    }

}
