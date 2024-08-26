package com.ILPex.service;

import com.ILPex.DTO.*;
import com.ILPex.entity.DailyReports;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyReportService {
    List<DailyReportDTO> getCourseDetailsWithTimeTaken(LocalDateTime courseDate, Long batchId, Long traineeId);

    DailyReportDetailsDTO getLearningPlanDetails(Long courseId, Long traineeId);
    DailyReportEditDTO getDailyReportEditDetails(Long dailyReportId);
    DailyReports addOrUpdateDailyReport(DailyReportRequestDTO dailyReportRequestDTO, Long traineeId, Long courseId);

//    List<DailyReportDTO> getDailyReportByBatchIdDateAndTraineeId(Long batchId, LocalDate courseDate, Long traineeId);
//    List<DailyReportCreationDTO> getDailyReportById(Long id);
//    DailyReportAddDTO addDailyReport(DailyReportAddDTO dailyReportAddDTO);
}
