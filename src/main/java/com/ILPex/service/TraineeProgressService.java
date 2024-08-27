package com.ILPex.service;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.DTO.TraineeDTO;

import java.util.List;
import java.util.Map;

public interface TraineeProgressService {
//    void updateCourseIdsInTraineeProgress();
void calculateLatestDayNumberForTrainees();
    Map<String, Integer> getProgressStatusCounts();
    Map<Long, Integer> getLastAccessedDayNumberForTrainees();
    Map<String, Integer> getLastAccessedDayNumberForTraineesName();
    Map<Long, Integer> getDayNumberForTrainees();
    List<CourseProgressDTO> getTraineeProgress(Long traineeId);
}

