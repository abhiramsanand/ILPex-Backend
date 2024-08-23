package com.ILPex.controller;

import com.ILPex.dto.ProfileDTO;
import com.ILPex.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public List<ProfileDTO> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    @GetMapping("/{traineeId}")
    public ProfileDTO getProfileByTraineeId(@PathVariable Long traineeId) {
        return profileService.getProfileByTraineeId(traineeId);
    }
}
