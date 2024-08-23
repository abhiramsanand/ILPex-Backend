package com.ILPex.service.Impl;

import com.ILPex.DTO.UserContentAccessDTO;
import com.ILPex.entity.UserContentAccess;
import com.ILPex.repository.UserContentAccessRepository;
import com.ILPex.service.UserContentAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Timestamp;

@Service
public class UserContentAccessServiceImpl implements UserContentAccessService {

    @Autowired
    private UserContentAccessRepository userContentAccessRepository;

    @Override
    public void saveUserContentAccess(UserContentAccessDTO userContentAccessDTO) {
        UserContentAccess userContentAccess = new UserContentAccess();
        userContentAccess.setUserId(userContentAccessDTO.getUserId());
        userContentAccess.setFirstName(userContentAccessDTO.getFirstName());
        userContentAccess.setLastName(userContentAccessDTO.getLastName());
        userContentAccess.setAudience(userContentAccessDTO.getAudience());
        userContentAccess.setContentUuid(UUID.fromString(userContentAccessDTO.getContentUuid()));
        userContentAccess.setContentTitle(userContentAccessDTO.getContentTitle());
        userContentAccess.setContentType(userContentAccessDTO.getContentType());
        userContentAccess.setLanguageCode(userContentAccessDTO.getLanguageCode());
        userContentAccess.setCategory(userContentAccessDTO.getCategory());
        userContentAccess.setSource(userContentAccessDTO.getSource());
        userContentAccess.setStatus(userContentAccessDTO.getStatus());
        userContentAccess.setCompletedDate(userContentAccessDTO.getCompletedDate());
        userContentAccess.setDuration(userContentAccessDTO.getDuration());
        userContentAccess.setEstimatedDuration(userContentAccessDTO.getEstimatedDuration());
        userContentAccess.setFirstAccess(userContentAccessDTO.getFirstAccess());
        userContentAccess.setLastAccess(userContentAccessDTO.getLastAccess());
        userContentAccess.setTotalAccesses(userContentAccessDTO.getTotalAccesses());
        userContentAccess.setEmailAddress(userContentAccessDTO.getEmailAddress());
        userContentAccess.setDurationHms(userContentAccessDTO.getDurationHms());
        userContentAccess.setEstimatedDurationHms(userContentAccessDTO.getEstimatedDurationHms());
        userContentAccess.setUserUuid(UUID.fromString(userContentAccessDTO.getUserUuid()));
        userContentAccess.setUserStatus(userContentAccessDTO.getUserStatus());

        userContentAccessRepository.save(userContentAccess);
    }

    @Override
    public List<UserContentAccessDTO> getAllUserContentAccess() {
        return List.of();
    }

//    @Override
//    public List<UserContentAccessDTO> getAllUserContentAccess() {
//        List<UserContentAccess> userContentAccessList = userContentAccessRepository.findAll();
//        return userContentAccessList.stream().map(this::convertToDTO).toList();
//    }

//    private UserContentAccessDTO convertToDTO(UserContentAccess userContentAccess) {
//        return new UserContentAccessDTO(
//                userContentAccess.getId(),
//                userContentAccess.getUserId(),
//                userContentAccess.getFirstName(),
//                userContentAccess.getLastName(),
//                userContentAccess.getAudience(),
//                userContentAccess.getContentUuid(),
//                userContentAccess.getContentTitle(),
//                userContentAccess.getContentType(),
//                userContentAccess.getLanguageCode(),
//                userContentAccess.getCategory(),
//                userContentAccess.getSource(),
//                userContentAccess.getStatus(),
//                userContentAccess.getCompletedDate(),
//                userContentAccess.getDuration(),
//                userContentAccess.getEstimatedDuration(),
//                userContentAccess.getFirstAccess(),
//                userContentAccess.getLastAccess(),
//                userContentAccess.getTotalAccesses(),
//                userContentAccess.getEmailAddress(),
//                userContentAccess.getDurationHms(),
//                userContentAccess.getEstimatedDurationHms(),
//                userContentAccess.getUserUuid(),
//                userContentAccess.getUserStatus()
//        );
//    }
}
