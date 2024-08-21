package com.ILPex.controller;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.service.TraineeProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/ilpex/traineeprogress")
public class TraineeProgressController {

    @Autowired
    private TraineeProgressService traineeProgressService;

    @GetMapping("/courseprogress/{traineeId}")
    public List<CourseProgressDTO> getCourseProgressByTraineeId(@PathVariable Long traineeId) {
        return traineeProgressService.getCourseProgressByTraineeId(traineeId);
    }
}