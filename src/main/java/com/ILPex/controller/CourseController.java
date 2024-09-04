package com.ILPex.controller;

import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Courses;
import com.ILPex.service.BatchService;
import com.ILPex.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:5173")
public class CourseController {

    @Autowired
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Autowired
    private BatchService batchService;

    @GetMapping("/batch/{batchId}")
    public List<CourseDayBatchDTO> getCoursesByBatchId(@PathVariable Long batchId) {
        return courseService.getCoursesByBatchId(batchId);
    }

    @GetMapping("/total-course-days-completed/{batchId}")
    public ResponseEntity<TotalCourseDaysDTO> getTotalCourseDaysCompleted(@PathVariable Long batchId) {
        TotalCourseDaysDTO totalCourseDays = courseService.getTotalCourseDaysCompleted(batchId);
        return ResponseEntity.ok(totalCourseDays);
    }

    @GetMapping("/total-duration/{batchId}")
    public ResponseEntity<TotalCourseDurationDTO> getTotalCourseDuration(@PathVariable Long batchId) {
        TotalCourseDurationDTO dto = courseService.getTotalCourseDuration(batchId);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/create")
    public ResponseEntity<String> createCourses(
            @RequestParam("batchId") Long batchId,
            @RequestParam("file") MultipartFile file) {

        Batches batch = batchService.getBatchById(batchId);
        if (batch == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Batch not found with ID " + batchId);
        }

        try {
            List<Courses> coursesList = courseService.parseCourseExcelFile(file, batch);
            courseService.saveCourses(coursesList);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing Excel file");
        }
        return ResponseEntity.ok("Courses created successfully");
    }

    @GetMapping("/WholeReport/{traineeId}/batch/{batchId}")     //Whole Reports
    public List<CourseDailyReportDTO> getCourseDetails(
            @PathVariable Long traineeId,
            @PathVariable Long batchId) {
        return courseService.getCourseDetails(traineeId, batchId);
    }

    @GetMapping("/pending-submissions")         //pending dailyrepots
    public List<PendingSubmissionDTO> getPendingSubmissions(@RequestParam Long batchId, @RequestParam Long traineeId) {
        return courseService.getPendingSubmissions(batchId, traineeId);
    }

    @GetMapping("/dates/dayNumber")
    public List<DayNumberWithDateDTO> getAllCourseDatesAndDayNumber() {
        return courseService.getAllCourseDatesWithDayNumber();
    }

    @PostMapping("/unmark-holiday")
    public ResponseEntity<String> unmarkHoliday(@RequestBody Map<String, String> payload) {
        String holidayDateStr = payload.get("holidayDate");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate holidayDate = LocalDate.parse(holidayDateStr, formatter);

        courseService.restoreCourseDatesForWorkingDay(holidayDate);

        return ResponseEntity.ok("Holiday unmarked and course dates updated successfully.");
    }

    @PostMapping("/mark-holiday/day")
    public String markHolidayDay(@RequestBody Map<String, String> payload) {
        String holidayDateStr = payload.get("holidayDate");
        String description = payload.get("description");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate holidayDate = LocalDate.parse(holidayDateStr, formatter);

        courseService.updateCourseDatesForHoliday(holidayDate, description);

        return "Holiday marked and course dates updated successfully.";
    }
    @GetMapping("/coursesWithProgress")
    public List<CourseTraineeProgressDTO> getCoursesWithProgress(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date courseDate,
            @RequestParam Long traineeId,
            @RequestParam Long batchId) {
        Timestamp courseDateTimestamp = new Timestamp(courseDate.getTime());
        return courseService.getCoursesWithProgress(traineeId, batchId, courseDateTimestamp);
    }
}

