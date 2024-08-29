package com.ILPex.service;

import com.ILPex.DTO.TraineeAverageScoreDTO;

public interface ResultsService {
    TraineeAverageScoreDTO getAverageScoreByTraineeId(Long traineeId);
}
