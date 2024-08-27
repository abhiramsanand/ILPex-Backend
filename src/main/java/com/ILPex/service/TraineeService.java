package com.ILPex.service;

import com.ILPex.DTO.TraineeDTO;

import java.util.List;

public interface TraineeService {
    List<TraineeDTO> getTraineesByBatchId(Long batchId);
}