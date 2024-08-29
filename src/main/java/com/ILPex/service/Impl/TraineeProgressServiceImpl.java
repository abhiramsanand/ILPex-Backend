package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.DTO.TraineeActualVsEstimatedDurationDTO;
import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeDurationDTO;
import com.ILPex.entity.*;
import com.ILPex.repository.*;
import com.ILPex.service.TraineeProgressService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

//    @Override
//    @PostConstruct
//    public void updateCourseIdsInTraineeProgress() {
//        List<TraineeProgress> traineeProgressList = traineeProgressRepository.findAll();
//        List<Courses> coursesList = coursesRepository.findAll();
//
//        for (TraineeProgress traineeProgress : traineeProgressList) {
//            for (Courses course : coursesList) {
//                if (traineeProgress.getCourseName().equals(course.getCourseName())) {
//                    traineeProgress.setCourses(course); // Set the course entity
//                    traineeProgressRepository.save(traineeProgress);
//                    break;
//                }
//            }
//        }
//    }

    private int aheadCount = 0;
    private int onTrackCount = 0;
    private int behindCount = 0;

    @Override
//    @PostConstruct
    public void calculateLatestDayNumberForTrainees() {
        // Fetch all unique trainee IDs
        List<Long> traineeIds = traineeProgressRepository.findDistinctTraineeIds();

        for (Long traineeId : traineeIds) {
            // Get the latest TraineeProgress by completed date for each trainee
            Optional<TraineeProgress> latestProgress = traineeProgressRepository.findTopByTrainees_IdOrderByCompletedDateDesc(traineeId);

            if (latestProgress.isPresent()) {
                String courseName = latestProgress.get().getCourseName();

                // Find the corresponding course in Courses by course name
                Optional<Courses> course = coursesRepository.findByCourseName(courseName);
                // Find the corresponding batch for the trainee
                Optional<Batches> batch = batchRepository.findByTrainees_Id(traineeId);

                if (course.isPresent() && batch.isPresent()) {
                    int traineeDayNumber = course.get().getDayNumber();
                    Long batchDayNumber = batch.get().getDayNumber();

                    if (batchDayNumber != null) {
                        // Compare using batchDayNumber.intValue() or convert traineeDayNumber to long
                        if (traineeDayNumber > batchDayNumber.intValue()) {
                            aheadCount++;
                        } else if (traineeDayNumber == batchDayNumber.intValue()) {
                            onTrackCount++;
                        } else {
                            behindCount++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Integer> getProgressStatusCounts() {
        Map<String, Integer> progressStatusCounts = new HashMap<>();
        progressStatusCounts.put("ahead", aheadCount);
        progressStatusCounts.put("onTrack", onTrackCount);
        progressStatusCounts.put("behind", behindCount);
        return progressStatusCounts;
    }

    @Override
    public Map<Long, Integer> getLastAccessedDayNumberForTrainees() {
        Map<Long, Integer> traineeDayNumberMap = new HashMap<>();

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

                // Set day_number in the map
                traineeDayNumberMap.put(traineeId, dayNumberOpt.orElse(0));
            } else {
                // No progress found, set day_number to 0
                traineeDayNumberMap.put(traineeId, 0);
            }
        }

        return traineeDayNumberMap;
    }

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
    public Map<Long, Integer> getDayNumberForTrainees() {
        Map<Long, Integer> traineeDayNumberMap = new HashMap<>();

        // Get all trainee IDs
        List<Trainees> traineesList = traineesRepository.findAll();

        for (Trainees trainee : traineesList) {
            Long traineeId = trainee.getId();

            // Get the latest daily report for the trainee
            List<DailyReports> reportsList = dailyReportsRepository.findDailyReportsByTraineeId(traineeId);

            if (!reportsList.isEmpty()) {
                DailyReports latestReport = reportsList.get(0); // Latest report based on the date

                Long courseId = latestReport.getCourses().getId();

                // Get the day number from the Courses entity
                Optional<Integer> dayNumberOpt = coursesRepository.findDayNumberByCourseId(courseId);

                // Set day_number in the map
                traineeDayNumberMap.put(traineeId, dayNumberOpt.orElse(0));
            } else {
                // No reports found, set day_number to 0
                traineeDayNumberMap.put(traineeId, 0);
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

    public List<TraineeActualVsEstimatedDurationDTO> getTotalDurationAndEstimatedDurationByTraineeIdAndBatch(Long batchId) {
        return traineeProgressRepository.findTotalDurationAndEstimatedDurationByBatchId(batchId);
    }

}