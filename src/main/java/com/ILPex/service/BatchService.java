package com.ILPex.service;

import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;

import java.util.List;

public interface BatchService {
    List<BatchDTO> getBatches();
    List<CourseDayBatchDTO> getDaywiseCoursesForAllBatches();
    BatchDTO calculateDayNumber(Long batchId);
    Batches createBatch(BatchCreationDTO batchCreationDTO);
    Batches getBatchById(Long batchId);
    List<TraineeDisplayByBatchDTO> getTraineesByBatchId(Long batchId);
    TraineeDisplayByBatchDTO addTraineeToBatch(Long batchId, TraineeCreationDTO traineeCreationDTO);
    void updateDayNumbers();
    TraineeDisplayByBatchDTO updateTrainee(Long traineeId, TraineeUpdateDTO traineeUpdateDTO);
    void updateAllTrainees(Long batchId, List<TraineeUpdateDTO> traineeDtos);
    Batches updateBatch(Long batchId, BatchUpdateDTO batchUpdateDTO);
    BatchDetailsDTO getBatchDetails(Long batchId);
    void deleteTrainee(Long traineeId);
}
