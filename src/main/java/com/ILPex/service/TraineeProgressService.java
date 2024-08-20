package com.ILPex.service;

import com.ILPex.DTO.CourseProgressDTO;

import java.util.List;

public interface TraineeProgressService {
    List<CourseProgressDTO> getCourseProgressByTraineeId(Long traineeId);


}
