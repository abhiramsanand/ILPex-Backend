package com.ILPex.controller;

import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.TrainingDaysCompletedForTraineeDTO;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.BatchService;
import com.ILPex.service.TraineeProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ILPex.constants.Messages.REQUESTED_TRAINEE_DETAILS;


@RestController
@RequestMapping("/api/v1/progress")
@CrossOrigin(origins = "http://localhost:5173")
public class TraineeProgressController {
    @Autowired
    private TraineeProgressService traineeProgressService;

    @GetMapping
    public ResponseEntity<List<TrainingDaysCompletedForTraineeDTO>> getProgress() {
        List<TrainingDaysCompletedForTraineeDTO> progressList = traineeProgressService.getNumberOfDays();
        return ResponseEntity.ok(progressList);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDayNumberById(@PathVariable(name = "id")long id){
        return ResponseHandler.responseBuilder(REQUESTED_TRAINEE_DETAILS,
                HttpStatus.OK,traineeProgressService.getDayNumberById(id));
    }


}
