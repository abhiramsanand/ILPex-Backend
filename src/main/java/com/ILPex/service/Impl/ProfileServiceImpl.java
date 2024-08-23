package com.ILPex.service.impl;

import com.ILPex.dto.ProfileDTO;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private TraineesRepository traineesRepository;

    @Override
    public List<ProfileDTO> getAllProfiles() {
        return traineesRepository.findAll().stream().map(this::convertToProfileDTO).collect(Collectors.toList());
    }

    @Override
    public ProfileDTO getProfileByTraineeId(Long traineeId) {
        return traineesRepository.findById(traineeId)
                .map(this::convertToProfileDTO)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
    }

    private ProfileDTO convertToProfileDTO(Trainees trainee) {
        return new ProfileDTO(
                trainee.getId(),
                trainee.getUsers().getUserName(),
                trainee.getBatches().getBatchName(),
                trainee.getUsers().getEmail()
        );
    }
}
