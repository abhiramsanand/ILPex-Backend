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
@RequestMapping("/api/v1/ilpex/traineeprogress")
public class TraineeProgressController {

    @Autowired
    private TraineeProgressService traineeProgressService;

    @GetMapping("/courseprogress/{traineeId}")
    public ResponseEntity<Object> getCourseProgressByTraineeId(@PathVariable Long traineeId) {
        List<CourseProgressDTO> courseProgress = traineeProgressService.getCourseProgressByTraineeId(traineeId);
        return ResponseHandler.responseBuilder(Message.REQUESTED_COURSE_PROGRESS_DETAILS, HttpStatus.OK, courseProgress);
    }
}