package com.ILPex.service.Impl;

import com.ILPex.DTO.TraineeAverageScoreDTO;
import com.ILPex.repository.ResultsRepository;
import com.ILPex.service.TraineeAverageScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TraineeAverageScoreServiceImpl  implements TraineeAverageScoreService {
    @Autowired
    private ResultsRepository resultsRepository;

    @Override
    public Optional<TraineeAverageScoreDTO> getAverageScoreByTraineeId(Long traineeId) {
        return resultsRepository.findAverageScoreByTraineeId(traineeId);
    }
}
