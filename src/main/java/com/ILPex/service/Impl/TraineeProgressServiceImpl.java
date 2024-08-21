package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.service.TraineeProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeProgressServiceImpl implements TraineeProgressService {

    @Autowired
    private TraineeProgressRepository traineeProgressRepository;

    @Override
    public List<CourseProgressDTO> getCourseProgressByTraineeId(Long traineeId) {
        return traineeProgressRepository.findCourseProgressByTraineeId(traineeId);
    }
}