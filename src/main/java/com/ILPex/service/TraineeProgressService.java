package com.ILPex.service;

import com.ILPex.DTO.CourseProgressDTO;

import java.util.List;
import java.util.Map;

public interface TraineeProgressService {
//    void updateCourseIdsInTraineeProgress();
void calculateLatestDayNumberForTrainees();
    Map<String, Integer> getProgressStatusCounts();
    Map<Long, Integer> getLastAccessedDayNumberForTrainees();
    Map<Long, Integer> getDayNumberForTrainees();
    List<CourseProgressDTO> getTraineeProgress(Long traineeId);
}

