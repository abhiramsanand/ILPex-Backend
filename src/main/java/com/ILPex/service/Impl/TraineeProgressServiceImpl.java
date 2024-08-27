package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Courses;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.TraineeProgressService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public List<CourseProgressDTO> getCourseProgressByTraineeId(Long traineeId) {
        return traineeProgressRepository.findCourseProgressByTraineeId(traineeId);
    }

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
    @PostConstruct
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
}