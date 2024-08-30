package com.ILPex.service;

import com.ILPex.DTO.UserContentAccessDTO;

import java.util.List;

public interface PercipioApiService {
    String generateRequestId();
    String fetchData(String requestId);
    void processDataAndSaveToDatabase();
    List<UserContentAccessDTO> parseJsonToDTO(String jsonData);
}
