package com.ILPex.service;

import com.ILPex.DTO.CourseProgressDTO;

import java.util.List;
import java.util.Map;

public interface TraineeProgressService {
    List<CourseProgressDTO> getCourseProgressByTraineeId(Long traineeId);
//    void updateCourseIdsInTraineeProgress();
void calculateLatestDayNumberForTrainees();
    Map<String, Integer> getProgressStatusCounts();

}

