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
    public Map<String, Double> getAverageScoresForAllTraineesByName() {
        Map<String, Double> traineeScores = new HashMap<>();

        // Fetch all distinct trainee IDs
        List<Long> traineeIds = resultsRepository.findDistinctTraineeIds();

        // Calculate the average score for each trainee and map it to their name
        for (Long traineeId : traineeIds) {
            List<Object[]> results = resultsRepository.findTraineeNameAndAverageScoreByTraineeId(traineeId);

            for (Object[] result : results) {
                // Ensure proper casting of each element in the Object array
                String traineeName = (String) result[0];
                Double averageScore = (Double) result[1];
                traineeScores.put(traineeName, averageScore);
            }
        }

        return traineeScores;
    }

}
