package com.ILPex.service.Impl;

import com.ILPex.DTO.UserContentAccessDTO;
import com.ILPex.entity.Batches;
import com.ILPex.repository.BatchRepository;
import com.ILPex.service.PercipioApiService;
import com.ILPex.service.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    @Autowired
    private PercipioApiService percipioApiService;

    @Autowired
    private UserContentAccessServiceImpl userContentAccessService;

    @Autowired
    private BatchRepository batchRepository;

    @Override
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void fetchAndSaveData() {
        // Step 1: Generate request ID
        String requestId = percipioApiService.generateRequestId();
        if (requestId == null || requestId.isEmpty()) {
            System.out.println("Failed to generate request ID");
            return;
        }

        // Step 2: Fetch data using the request ID
        String jsonData = percipioApiService.fetchData(requestId);
        if (jsonData == null || jsonData.isEmpty()) {
            System.out.println("Failed to fetch data");
            return;
        }

        // Step 3: Parse JSON and convert to DTO
        List<UserContentAccessDTO> userContentAccessList = parseJsonToDTO(jsonData);

        // Step 4: Save data to database
        userContentAccessList.forEach(userContentAccessService::saveUserContentAccess);
    }

    private List<UserContentAccessDTO> parseJsonToDTO(String jsonData) {
        // Implement JSON parsing logic here to convert JSON string to List<UserContentAccessDTO>
        // Use libraries such as Jackson or Gson to parse the JSON
        // Example: ObjectMapper or Gson can be used to map JSON to DTO objects
        // This is a placeholder, actual implementation will depend on the JSON structure
        return new ArrayList<>(); // Replace with actual parsing logic
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    @Transactional
    public void deactivateOldBatches() {
        LocalDate today = LocalDate.now();
        List<Batches> batchesList = batchRepository.findAll();

        for (Batches batch : batchesList) {
            LocalDate batchEndDate = batch.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Check if 10 days have passed since the batch end date
            if (today.isAfter(batchEndDate.plusDays(10))) {
                batch.setIsActive(false);
                batchRepository.save(batch);
            }
        }
    }
}
