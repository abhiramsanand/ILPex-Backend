package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.service.TraineeProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class TraineeProgressServiceImpl implements TraineeProgressService {

    @Autowired
    private TraineeProgressRepository traineeProgressRepository;

    @Override
    public List<CourseProgressDTO> getTraineeProgress(Long traineeId) {
        List<Object[]> results = traineeProgressRepository.findCourseProgressByTraineeId(traineeId);
        return results.stream()
                .map(result -> {
                    String courseName = (String) result[0];
                    Integer dayNumber = (Integer) result[1];
                    Integer estimatedDuration = (Integer) result[2];
                    Integer duration = (Integer) result[3];
                    Integer percentageCompleted = calculatePercentageCompleted(duration, estimatedDuration);

                    return new CourseProgressDTO(
                            courseName,
                            dayNumber,
                            estimatedDuration,
                            duration,
                            percentageCompleted
                    );
                })
                .collect(Collectors.toList());
    }

    // Method to calculate percentage completed
    private Integer calculatePercentageCompleted(Integer duration, Integer estimatedDuration) {
        if (estimatedDuration != null && estimatedDuration > 0) {
            double percentage = ((double) duration / estimatedDuration) * 100;
            // Round to the nearest integer and cap at 100%
            return (int) Math.min(Math.round(percentage), 100);
        }
        return 0; // Return 0% if estimatedDuration is not valid
    }


}
