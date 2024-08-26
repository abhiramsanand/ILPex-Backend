package com.ILPex.service.Impl;

import com.ILPex.DTO.TraineeDisplayByBatchDTO;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.TraineeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {
    @Autowired
    private TraineesRepository traineeRepository;


//    public void updateTrainees(Long batchId, List<TraineeDisplayByBatchDTO> traineeDtos) {
//
//            // Save updated trainee
//            traineeRepository.save(trainee);
//        }
//    }

}
