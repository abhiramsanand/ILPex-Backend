package com.ILPex.service;

import com.ILPex.DTO.*;

import java.util.List;
import java.util.Map;

public interface TraineeProgressService {
    List<Map<String, Object>> getLastAccessedDayNumberForTrainees();
    List<CourseProgressDTO> getTraineeProgress(Long traineeId);
    List<TraineeCourseDurationDTO> findTotalCourseDurationDTOByBatchId(Long batchId);
    List<TraineeCourseCountDTO> getDistinctCourseDurationCountByBatchId(Long batchId);
    List<TraineeActualVsEstimatedDurationDTO> getTotalDurationAndEstimatedDurationByTraineeIdAndBatch(Long BatchId);

}

