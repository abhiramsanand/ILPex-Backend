package com.ILPex.controller;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.constants.Message;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.TraineeProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trainee-progress")
public class TraineeProgressController {


    @Autowired
    private TraineeProgressService traineeProgressService;

    @GetMapping("/{traineeId}")
    public List<CourseProgressDTO> getTraineeProgress(@PathVariable Long traineeId) {
        return traineeProgressService.getTraineeProgress(traineeId);
    }
}