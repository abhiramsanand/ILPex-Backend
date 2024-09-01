package com.ILPex.controller;


import com.ILPex.DTO.PendingCourseDTO;
import com.ILPex.service.PendingCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainees")
public class PendingCourseController {

    @Autowired
    private PendingCourseService pendingCourseService;

    @GetMapping("/{traineeId}/pending-courses")
    public ResponseEntity<List<PendingCourseDTO>> getPendingCoursesForToday(@PathVariable Long traineeId) {
        List<PendingCourseDTO> pendingCourses = pendingCourseService.getPendingCoursesForToday(traineeId);
        return ResponseEntity.ok(pendingCourses);
    }
}