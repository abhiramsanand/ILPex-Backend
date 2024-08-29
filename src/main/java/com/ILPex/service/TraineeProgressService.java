package com.ILPex.service;

import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeCourseCountDTO;
import com.ILPex.DTO.TraineeCourseDurationDTO;

import java.util.List;
import java.util.Map;

public interface TraineeProgressService {
    Map<String, Integer> getLastAccessedDayNumberForTraineesName();
    List<CourseProgressDTO> getTraineeProgress(Long traineeId);
    List<TraineeCourseDurationDTO> findTotalCourseDurationDTOByBatchId(Long batchId);
    List<TraineeCourseCountDTO> getDistinctCourseDurationCountByBatchId(Long batchId);
}

