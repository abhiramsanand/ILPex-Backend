package com.ILPex.controller;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/batch/{batchId}")
    public List<CourseDayBatchDTO> getCoursesByBatchId(@PathVariable Long batchId) {
        return courseService.getCoursesByBatchId(batchId);
    }
}

