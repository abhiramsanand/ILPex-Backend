package com.ILPex.service;

import com.ILPex.DTO.*;

import java.time.LocalDate;
import java.util.List;

public interface DailyReportService {
    List<DailyReportDTO> getDailyReport(Long trainees, LocalDate reportDate);
    List<DailyReportCreationDTO> getDailyReportById(Long id);
    DailyReportAddDTO addDailyReport(DailyReportAddDTO dailyReportAddDTO);
}
