package com.ILPex.service;

import com.ILPex.DTO.TraineeAverageScoreDTO;

import java.util.Optional;

public interface TraineeAverageScoreService {
    Optional<TraineeAverageScoreDTO> getAverageScoreByTraineeId(Long traineeId);
}
