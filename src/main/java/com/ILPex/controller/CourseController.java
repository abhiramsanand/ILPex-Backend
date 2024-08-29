package com.ILPex.controller;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.DTO.CourseDurationDTO;
import com.ILPex.DTO.TotalCourseDaysDTO;
import com.ILPex.DTO.TotalCourseDurationDTO;
import com.ILPex.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/batch/{batchId}")
    public List<CourseDayBatchDTO> getCoursesByBatchId(@PathVariable Long batchId) {
        return courseService.getCoursesByBatchId(batchId);
    }

//    @GetMapping("/total-duration")
//    public CourseDurationDTO getTotalCourseDuration(@RequestParam Long batchId) {
//        return courseService.getTotalCourseDuration(batchId);
//    }

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
}

