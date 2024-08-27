package com.ILPex.service;

import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;

import java.io.InputStream;
import java.util.List;

public interface BatchService {
    List<BatchDTO> getBatches();

    List<CourseDayBatchDTO> getDaywiseCoursesForAllBatches();
   Batches createBatch(BatchCreationDTO batchCreationDTO);
    Batches getBatchById(Long batchId);
    List<TraineeDisplayByBatchDTO> getTraineesByBatchId(Long batchId);
    TraineeDisplayByBatchDTO addTraineeToBatch(Long batchId, TraineeCreationDTO traineeCreationDTO);
    void deleteTrainee(Long traineeId);
    BatchDetailsDTO getBatchDetails(Long batchId);
    TraineeDisplayByBatchDTO updateTrainee(Long traineeId, TraineeUpdateDTO traineeUpdateDTO);
//
//void updateBatchAndTrainees(Long batchId, BatchDetailsDTO batchDetailsDTO, List<TraineeUpdateDTO> traineeDtos);
void updateAllTrainees(Long batchId, List<TraineeUpdateDTO> traineeDtos);
    Batches updateBatch(Long batchId, BatchUpdateDTO batchUpdateDTO);
}