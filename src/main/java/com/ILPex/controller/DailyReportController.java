package com.ILPex.controller;

import com.ILPex.DTO.*;
import com.ILPex.service.DailyReportService;
import com.ILPex.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dailyreport")
public class DailyReportController {
    @Autowired
    private DailyReportService dailyReportService;

    @GetMapping("/view/{id}/{date}")        //api to display daily report
    public ResponseEntity<List<DailyReportDTO>> getDailyReport(
            @PathVariable("id") Long id,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {
        // Call the service method with the provided traineeId and reportDate
        List<DailyReportDTO> dailyReportList = dailyReportService.getDailyReport(id, reportDate);
        return ResponseEntity.ok(dailyReportList);
    }
    @GetMapping("/viewReport/{id}")        //api to display daily report
    public ResponseEntity<List<DailyReportCreationDTO>> getDailyReport(
            @PathVariable("id") Long id ){
        // Call the service method with the provided traineeId and reportDate
        List<DailyReportCreationDTO> dailyReportList = dailyReportService.getDailyReportById(id);
        return ResponseEntity.ok(dailyReportList);
    }

    @PostMapping("/save/{courseId}")
    public ResponseEntity<DailyReportAddDTO> addDailyReport(
            @PathVariable("courseId") Long courseId,
            @RequestBody DailyReportAddDTO dailyReportAddDTO) {

        // Set the courseId from the path variable
        dailyReportAddDTO.setCourseId(courseId);

        // Call the service to add the daily report
        DailyReportAddDTO createdReport = dailyReportService.addDailyReport(dailyReportAddDTO);

        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }

}
