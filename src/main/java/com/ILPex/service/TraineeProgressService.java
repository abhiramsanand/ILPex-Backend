package com.ILPex.service;

import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.TrainingDaysCompletedForTraineeDTO;

import java.util.List;

public interface TraineeProgressService  {
    List<TrainingDaysCompletedForTraineeDTO> getNumberOfDays();
    TrainingDaysCompletedForTraineeDTO getDayNumberById(Long traineeId);
}
