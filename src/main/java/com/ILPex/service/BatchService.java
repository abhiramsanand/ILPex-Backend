package com.ILPex.service;

import com.ILPex.DTO.BatchCreationDTO;
import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.entity.Batches;

import java.io.InputStream;
import java.util.List;

public interface BatchService {
    List<BatchDTO> getBatches();

    List<CourseDayBatchDTO> getDaywiseCoursesForAllBatches();
   Batches createBatch(BatchCreationDTO batchCreationDTO);
     Batches getBatchById(Long batchId);
}
