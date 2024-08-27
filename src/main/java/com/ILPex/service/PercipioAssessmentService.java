package com.ILPex.service;

import java.util.Map;

public interface PercipioAssessmentService {
    Map<Long, Double> getAverageScoresForAllTrainees();
     Map<String, Double> getAverageScoresForAllTraineesWithName();
}
