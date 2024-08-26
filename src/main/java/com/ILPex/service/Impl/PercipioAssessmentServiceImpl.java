package com.ILPex.service.Impl;

import com.ILPex.entity.PercipioAssessment;
import com.ILPex.repository.PercipioAssessmentRepository;
import com.ILPex.service.PercipioAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PercipioAssessmentServiceImpl implements PercipioAssessmentService {

    @Autowired
    private PercipioAssessmentRepository percipioAssessmentRepository;

    @Override
    public Map<Long, Double> getAverageScoresForAllTrainees() {
        List<PercipioAssessment> allAssessments = percipioAssessmentRepository.findAll();

        // Group assessments by traineeId, filter out scores of zero, and calculate average score
        Map<Long, Double> averageScores = allAssessments.stream()
                .filter(assessment -> assessment.getScore() > 0) // Exclude assessments with score of zero
                .collect(Collectors.groupingBy(
                        assessment -> assessment.getTrainees().getId(),
                        Collectors.averagingDouble(PercipioAssessment::getScore)
                ));

        return averageScores;
    }
}
