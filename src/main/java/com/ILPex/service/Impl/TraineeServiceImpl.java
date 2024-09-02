package com.ILPex.service.Impl;



import com.ILPex.DTO.TraineeDTO;

import com.ILPex.DTO.TraineeDailyReportDTO;
import com.ILPex.DTO.TraineeDayProgressDTO;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public List<TraineeDayProgressDTO> getLastDayForTrainees(Long batchId) {
        List<Object[]> results = traineesRepository.findLastDayForTraineesInBatch(batchId);
        List<TraineeDayProgressDTO> dtoList = new ArrayList<>();

        for (Object[] result : results) {
            String traineeName = (String) result[0];
            Integer lastDayNumber = (Integer) result[1];
            dtoList.add(new TraineeDayProgressDTO(traineeName, lastDayNumber));
        }

        return dtoList;
    }
}