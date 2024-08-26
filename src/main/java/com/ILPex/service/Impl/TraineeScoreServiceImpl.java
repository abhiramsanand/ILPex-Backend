package com.ILPex.service.Impl;

import com.ILPex.repository.ResultsRepository;
import com.ILPex.service.TraineeScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TraineeScoreServiceImpl implements TraineeScoreService {

    @Autowired
    private ResultsRepository resultsRepository;

    @Override
    public Map<Long, Double> getAverageScoresForAllTrainees() {
        Map<Long, Double> traineeScores = new HashMap<>();

        // Fetch all distinct trainee IDs
        List<Long> traineeIds = resultsRepository.findDistinctTraineeIds();

        // Calculate the average score for each trainee
        for (Long traineeId : traineeIds) {
            Double averageScore = resultsRepository.findAverageScoreByTraineeId(traineeId);
            traineeScores.put(traineeId, averageScore);
        }

        return traineeScores;
    }
}
