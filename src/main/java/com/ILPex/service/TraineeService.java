package com.ILPex.service;

import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeDailyReportDTO;

import java.util.List;

public interface TraineeService {
    List<TraineeDTO> getTraineesByBatchId(Long batchId);
    List<TraineeDailyReportDTO> getTraineeReportsByBatchId(Long batchId);
}