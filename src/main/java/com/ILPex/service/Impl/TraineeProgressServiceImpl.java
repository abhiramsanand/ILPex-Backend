package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.entity.Courses;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.service.TraineeProgressService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeProgressServiceImpl implements TraineeProgressService {

    @Autowired
    private TraineeProgressRepository traineeProgressRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @Override
    public List<CourseProgressDTO> getCourseProgressByTraineeId(Long traineeId) {
        return traineeProgressRepository.findCourseProgressByTraineeId(traineeId);
    }

    @Override
    @PostConstruct
    public void updateCourseIdsInTraineeProgress() {
        List<TraineeProgress> traineeProgressList = traineeProgressRepository.findAll();
        List<Courses> coursesList = coursesRepository.findAll();

        for (TraineeProgress traineeProgress : traineeProgressList) {
            for (Courses course : coursesList) {
                if (traineeProgress.getCourseName().equals(course.getCourseName())) {
                    traineeProgress.setCourses(course); // Set the course entity
                    traineeProgressRepository.save(traineeProgress);
                    break;
                }
            }
        }
    }

}