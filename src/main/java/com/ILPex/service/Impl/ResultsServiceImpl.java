package com.ILPex.service.Impl;

import com.ILPex.DTO.TraineeAverageScoreDTO;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.ResultsRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.ResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResultsServiceImpl implements ResultsService {
    @Autowired
    private ResultsRepository resultsRepository;

    @Autowired
    private TraineesRepository traineesRepository;

    @Override
    public TraineeAverageScoreDTO getAverageScoreByTraineeId(Long traineeId) {
        Double averageScore = resultsRepository.findAverageScoreByTraineeId(traineeId);

        if (averageScore == null) {
            averageScore = 0.0;
        }

        Trainees trainee = traineesRepository.findById(traineeId).orElse(null);
        String username = trainee != null ? trainee.getUsers().getUserName() : null;

        return new TraineeAverageScoreDTO(username, averageScore);
    }
}
