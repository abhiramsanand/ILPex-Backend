package com.ILPex.service.Impl;

import com.ILPex.DTO.TraineeDurationDTO;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.TraineeDurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TraineeDurationServiceImpl implements TraineeDurationService {

    @Autowired
    private TraineesRepository traineesRepository;

    @Override
    public double calculateAcceleratedPercentage(Long batchId) {
        List<Trainees> traineesList = traineesRepository.findByBatchesId(batchId);
        if (traineesList.isEmpty()) {
            return 0;
        }

        long totalTrainees = traineesList.size();
        long acceleratedTrainees = traineesList.stream()
                .flatMap(trainee -> trainee.getTraineeProgresses().stream())
                .filter(progress -> progress.getDuration() < progress.getEstimatedDuration())
                .map(TraineeProgress::getTrainees)
                .distinct()
                .count();

        return (double) acceleratedTrainees / totalTrainees * 100;
    }
}
