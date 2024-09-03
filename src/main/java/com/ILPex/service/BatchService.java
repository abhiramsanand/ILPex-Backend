package com.ILPex.service;

import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BatchService {
    List<BatchDTO> getBatches();
    List<CourseDayBatchDTO> getDaywiseCoursesForAllBatches();
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
    Batches createBatchWithTrainees(BatchCreationDTO batchCreationDTO, MultipartFile file) throws IOException;
}
