package com.ILPex.service.Impl;

import com.ILPex.DTO.TraineeReportStatusDTO;
import com.ILPex.entity.DailyReports;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.TraineeReportStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TraineeReportStatusServiceImpl implements TraineeReportStatusService {

    @Autowired
    private TraineesRepository traineesRepository;

    @Override
    public List<TraineeReportStatusDTO> findLaggingTrainees() {
        List<Trainees> traineesList = traineesRepository.findAll();
        List<TraineeReportStatusDTO> result = new ArrayList<>();

        for (Trainees trainee : traineesList) {
            DailyReports lastReport = trainee.getDailyReports().stream()
                    .max(Comparator.comparing(DailyReports::getDate))
                    .orElse(null);

            if (lastReport != null) {
                int lastSubmittedDayNumber = lastReport.getCourses().getDayNumber();
                LocalDate startDate = trainee.getBatches().getStartDate().toLocalDateTime().toLocalDate();
                int actualDayNumber = (int) ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;

                boolean isLagging = lastSubmittedDayNumber < actualDayNumber;

                if (isLagging) {
                    TraineeReportStatusDTO dto = new TraineeReportStatusDTO(
                            trainee.getId(),
                            trainee.getUsers().getUserName(),
                            lastReport.getCourses().getCourseName(),
                            lastSubmittedDayNumber,
                            actualDayNumber,
                            true,
                            trainee.getBatches().getId()  // Include the batchId
                    );
                    result.add(dto);
                }
            }
        }
        return result;
    }

    @Override
    public double calculateLaggingPercentage(Long batchId) {
        List<Trainees> traineesList = traineesRepository.findByBatchesId(batchId);
        int totalTrainees = traineesList.size();
        long laggingTrainees = traineesList.stream()
                .filter(this::isTraineeLagging)
                .count();

        return totalTrainees > 0 ? (double) laggingTrainees / totalTrainees * 100 : 0;
    }

    private boolean isTraineeLagging(Trainees trainee) {
        DailyReports lastReport = trainee.getDailyReports().stream()
                .max(Comparator.comparing(DailyReports::getDate))
                .orElse(null);

        if (lastReport == null) {
            return false;
        }

        int lastSubmittedDayNumber = lastReport.getCourses().getDayNumber();
        LocalDate startDate = trainee.getBatches().getStartDate().toLocalDateTime().toLocalDate();
        int actualDayNumber = (int) ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;

        return lastSubmittedDayNumber < actualDayNumber;
    }
}
