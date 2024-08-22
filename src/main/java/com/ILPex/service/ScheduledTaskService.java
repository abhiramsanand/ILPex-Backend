package com.ILPex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    @Autowired
    private PercipioApiService percipioApiService;

    @Scheduled(cron = "0 0 * * * *")  // Adjust cron expression as needed
    public void scheduledFetchData() {
        String requestId = percipioApiService.generateRequestId();
        String data = percipioApiService.fetchData(requestId);
        // Process the data as needed
    }
}

