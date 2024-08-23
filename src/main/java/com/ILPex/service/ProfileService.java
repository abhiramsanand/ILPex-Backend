package com.ILPex.service;

import com.ILPex.dto.ProfileDTO;

import java.util.List;

public interface ProfileService {
    List<ProfileDTO> getAllProfiles();
    ProfileDTO getProfileByTraineeId(Long traineeId);
}
