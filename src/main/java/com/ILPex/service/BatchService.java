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


}
