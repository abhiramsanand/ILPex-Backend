package com.ILPex.service;

import com.ILPex.DTO.TraineeReportStatusDTO;
import java.util.List;

public interface TraineeReportStatusService {
    List<TraineeReportStatusDTO> findLaggingTrainees();
    double calculateLaggingPercentage(Long batchId);
}
