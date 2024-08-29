package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeCourseCountDTO;
import com.ILPex.DTO.TraineeCourseDurationDTO;
import com.ILPex.entity.*;
import com.ILPex.repository.*;
import com.ILPex.service.TraineeProgressService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeProgressServiceImpl implements TraineeProgressService {

    @Autowired
    private TraineeProgressRepository traineeProgressRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private TraineesRepository traineesRepository;

    @Autowired
    private DailyReportsRepository dailyReportsRepository;

    @Override
    public Map<String, Integer> getLastAccessedDayNumberForTraineesName() {
        Map<String, Integer> traineeDayNumberMap = new HashMap<>();

        // Get all trainee IDs
        List<Trainees> traineesList = traineesRepository.findAll();

        for (Trainees trainee : traineesList) {
            Long traineeId = trainee.getId();

            // Get all progress for the trainee
            List<TraineeProgress> progressList = traineeProgressRepository.findProgressByTraineeId(traineeId);

            if (!progressList.isEmpty()) {
                // Get the latest progress entry
                TraineeProgress latestProgress = progressList.get(0); // assuming the list is ordered by completedDate

                String courseName = latestProgress.getCourseName();

                // Get the day number from the Courses entity
                Optional<Integer> dayNumberOpt = coursesRepository.findDayNumberByCourseName(courseName);

                // Fetch trainee's name from the associated Users entity
                Users user = trainee.getUsers();
                String traineeName = user.getUserName();

                // Set day_number in the map with traineeName as key
                traineeDayNumberMap.put(traineeName, dayNumberOpt.orElse(0));
            } else {
                // No progress found, set day_number to 0
                // Fetch trainee's name from the associated Users entity
                Users user = trainee.getUsers();
                String traineeName = user.getUserName();

                traineeDayNumberMap.put(traineeName, 0);
            }
        }

        return traineeDayNumberMap;
    }

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

    @Override
    public List<TraineeCourseDurationDTO> findTotalCourseDurationDTOByBatchId(Long batchId) {
        List<Object[]> results = traineeProgressRepository.findTotalCourseDurationByBatchId(batchId);
        return results.stream()
                .map(result -> new TraineeCourseDurationDTO(
                        (String) result[0], // userName
                        ((Number) result[1]).longValue())) // totalDuration
                .collect(Collectors.toList());
    }

    @Override
    public List<TraineeCourseCountDTO> getDistinctCourseDurationCountByBatchId(Long batchId) {
        // Fetch results from repository
        List<Object[]> results = traineeProgressRepository.getDistinctCourseDurationCountByBatchId(batchId);

        // Convert results to DTOs
        return results.stream()
                .map(result -> new TraineeCourseCountDTO(
                        (String) result[0], // trainee_name
                        ((Number) result[1]).longValue())) // distinct_course_duration_count
                .collect(Collectors.toList());
    }

}