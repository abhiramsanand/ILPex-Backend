package com.ILPex.service;

import com.ILPex.DTO.UserContentAccessDTO;
import java.util.List;

public interface UserContentAccessService {
    void saveUserContentAccess(UserContentAccessDTO userContentAccessDTO);
    List<UserContentAccessDTO> getAllUserContentAccess();
}
