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

    @GetMapping("/courseDetails")
    public List<DailyReportDTO> getCourseDetailsWithTimeTaken(
            @RequestParam("courseDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime courseDate,
            @RequestParam("batchId") Long batchId,
            @RequestParam("traineeId") Long traineeId) {

        return dailyReportService.getCourseDetailsWithTimeTaken(courseDate, batchId, traineeId);
    }

    @GetMapping("/learningPlanDetails")
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

    @GetMapping("/editDetails")
    public DailyReportEditDTO getDailyReportEditDetails(@RequestParam Long dailyReportId) {
        return dailyReportService.getDailyReportEditDetails(dailyReportId);
    }

        @PostMapping("/updateDetails")
    public ResponseEntity<DailyReports> addOrUpdateDailyReport(

            @RequestBody DailyReportRequestDTO dailyReportRequestDTO,
            @RequestParam Long traineeId,
            @RequestParam Long courseId) {

        DailyReports updatedReport = dailyReportService.addOrUpdateDailyReport(dailyReportRequestDTO, traineeId, courseId);
        return ResponseEntity.ok(updatedReport);
    }
//    @GetMapping("/view/{batch_id}/{date}/{trainee_id}")
//    public ResponseEntity<List<DailyReportDTO>> getDailyReportByBatchAndDateAndTrainee(
//            @PathVariable("batch_id") Long batchId,
//            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate courseDate,
//            @PathVariable("trainee_id") Long traineeId) {
//        // Call the service method with the provided batchId, courseDate, and traineeId
//        List<DailyReportDTO> dailyReportList = dailyReportService.getDailyReportByBatchIdDateAndTraineeId(batchId, courseDate, traineeId);
//        return ResponseEntity.ok(dailyReportList);
//    }


//
//    @GetMapping("/viewReport/{id}")        //api to display daily report
//    public ResponseEntity<List<DailyReportCreationDTO>> getDailyReport(
//            @PathVariable("id") Long id ){
//        // Call the service method with the provided traineeId and reportDate
//        List<DailyReportCreationDTO> dailyReportList = dailyReportService.getDailyReportById(id);
//        return ResponseEntity.ok(dailyReportList);
//    }

//    @PostMapping("/save/{courseId}")
//    public ResponseEntity<DailyReportAddDTO> addDailyReport(
//            @PathVariable("courseId") Long courseId,
//            @RequestBody DailyReportAddDTO dailyReportAddDTO) {
//
//        // Set the courseId from the path variable
//        dailyReportAddDTO.setCourseId(courseId);
//
//        // Call the service to add the daily report
//        DailyReportAddDTO createdReport = dailyReportService.addDailyReport(dailyReportAddDTO);
//
//        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
//    }

}
