package com.ILPex.service.Impl;



import com.ILPex.DTO.TraineeDTO;

import com.ILPex.DTO.TraineeDailyReportDTO;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {

    @Autowired
    private TraineesRepository traineesRepository;

    @Override
    public List<TraineeDTO> getTraineesByBatchId(Long batchId) {
        return traineesRepository.findTraineesByBatchId(batchId);
    }

    @Override
    public List<TraineeDailyReportDTO> getTraineeReportsByBatchId(Long batchId) {
        return traineesRepository.findTraineeReportsByBatchId(batchId);
    }
}