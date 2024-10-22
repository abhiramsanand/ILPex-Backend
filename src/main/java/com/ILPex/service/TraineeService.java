package com.ILPex.service;

import com.ILPex.DTO.TraineeDTO;
import com.ILPex.DTO.TraineeDailyReportDTO;
import com.ILPex.DTO.TraineeDayProgressDTO;
import com.ILPex.DTO.TraineeProfileDTO;

import java.util.List;

public interface TraineeService {
    List<TraineeDTO> getTraineesByBatchId(Long batchId);
    List<TraineeDailyReportDTO> getTraineeReportsByBatchId(Long batchId);
    public Long getCurrentBatchDayNumber(Long traineeId);
    public List<TraineeDayProgressDTO> getLastDayForTrainees(Long batchId);
    TraineeProfileDTO getTraineeProfile(Long traineeId);
}