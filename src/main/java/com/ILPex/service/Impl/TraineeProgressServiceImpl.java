package com.ILPex.service.Impl;

import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.TrainingDaysCompletedForTraineeDTO;
import com.ILPex.DTO.UserDTO;
import com.ILPex.entity.TraineeProgress;
import com.ILPex.entity.Users;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.TraineeProgressRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.TraineeProgressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class TraineeProgressServiceImpl implements TraineeProgressService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TraineeProgressRepository traineeProgressRepository;

    @Autowired
    private TraineesRepository traineesRepository;
    @Override
    public List<TrainingDaysCompletedForTraineeDTO> getNumberOfDays() {
        List<TraineeProgress> progressList = traineeProgressRepository.findAll();
        return progressList.stream().map(progress -> {
            return modelMapper.map(progress, TrainingDaysCompletedForTraineeDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public TrainingDaysCompletedForTraineeDTO getDayNumberById(Long traineeId) {
        TraineeProgress traineeProgress = traineeProgressRepository.findById(traineeId).orElseThrow(()-> new ResourceNotFoundException("TraineeProgress","traineeId",traineeId));

        return modelMapper.map(traineeProgress,TrainingDaysCompletedForTraineeDTO.class);
    }


}
