package com.ILPex.service.Impl;

import com.ILPex.DTO.TraineeAssessmentDTO;
import com.ILPex.DTO.TraineeAssessmentDisplayDTO;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.TraineeAssessmentRepository;
import com.ILPex.service.TraineeAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeAssessmentServiceImpl implements TraineeAssessmentService {

    private final TraineeAssessmentRepository traineeAssessmentRepository;

    @Autowired
    public TraineeAssessmentServiceImpl(TraineeAssessmentRepository traineeAssessmentRepository) {
        this.traineeAssessmentRepository = traineeAssessmentRepository;
    }

    @Override
    public List<TraineeAssessmentDTO> getTraineeAssessmentDetails() {
        return traineeAssessmentRepository.fetchTraineeAssessmentDetails();
    }


}
