package com.ILPex.service.Impl;



import com.ILPex.DTO.TraineeDTO;

import com.ILPex.DTO.TraineeDailyReportDTO;
import com.ILPex.DTO.TraineeProfileDTO;
import com.ILPex.entity.Trainees;
import com.ILPex.DTO.TraineeDayProgressDTO;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Long getCurrentBatchDayNumber(Long traineeId) {
        // Find the trainee by ID
        Trainees trainee = traineesRepository.findById(traineeId)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with ID: " + traineeId));

        // Get the batch associated with the trainee
        return trainee.getBatches().getDayNumber();
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

    @Override
    public TraineeProfileDTO getTraineeProfile(Long traineeId) {
        // Fetch trainee by id
        Optional<Trainees> traineeOptional = traineesRepository.findById(traineeId);
        if (traineeOptional.isPresent()) {
            Trainees trainee = traineeOptional.get();

            // Build and return the profile DTO
            TraineeProfileDTO profileDTO = new TraineeProfileDTO();
            profileDTO.setUserName(trainee.getUsers().getUserName());
            profileDTO.setBatchName(trainee.getBatches().getBatchName());
            profileDTO.setEmail(trainee.getUsers().getEmail());

            return profileDTO;
        } else {
            // Handle case where traineeId is not found (could throw an exception or return null)
            throw new RuntimeException("Trainee with ID " + traineeId + " not found");
        }
    }
}