package com.ILPex.service;

public interface ScheduledTaskService {

    void fetchAndSaveData();

    void deactivateOldBatches();
}
