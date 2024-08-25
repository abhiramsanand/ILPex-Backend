package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.Courses;
import com.ILPex.entity.DailyReports;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.DailyReportsRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.DailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class DailyReportServiceImpl implements DailyReportService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DailyReportsRepository dailyReportsRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private TraineesRepository traineesRepository;

    @Override
    public List<DailyReportDTO> getDailyReport(Long traineeId, LocalDate reportDate) {
        return dailyReportsRepository.findByTrainees_IdAndDate(traineeId, reportDate)
                .stream()
                .map(dailyReport -> {
                    DailyReportDTO dailyReportDTO = modelMapper.map(dailyReport, DailyReportDTO.class);
                    dailyReportDTO.setCourseName(coursesRepository.findById(dailyReport.getCourses().getId())
                            .map(course -> course.getCourseName())
                            .orElse("Unknown Course"));
                    return dailyReportDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyReportCreationDTO> getDailyReportById(Long id) {
        DailyReports dailyReport = dailyReportsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Map the entity to the DTO
        DailyReportCreationDTO dailyReportCreationDTO = modelMapper.map(dailyReport, DailyReportCreationDTO.class);

        // Set the course name and course ID manually
        dailyReportCreationDTO.setCourseName(dailyReport.getCourses().getCourseName());
        dailyReportCreationDTO.setCourseId(dailyReport.getCourses().getId());

        return List.of(dailyReportCreationDTO);
    }

    @Override
    public DailyReportAddDTO addDailyReport(DailyReportAddDTO dailyReportAddDTO) {
        // Retrieve Course entity by courseId
        Optional<Courses> course = coursesRepository.findById(dailyReportAddDTO.getCourseId());
        if (course.isEmpty()) {
            throw new RuntimeException("Course not found with id: " + dailyReportAddDTO.getCourseId());
        }

        // Retrieve Trainee entity by traineeId
        Optional<Trainees> trainee = traineesRepository.findById(dailyReportAddDTO.getTraineeId());
        if (trainee.isEmpty()) {
            throw new RuntimeException("Trainee not found with id: " + dailyReportAddDTO.getTraineeId());
        }

        // Create and populate DailyReports entity
        DailyReports dailyReports = new DailyReports();
        dailyReports.setDate(dailyReportAddDTO.getDate());
//        dailyReports.setTimeTaken(dailyReportAddDTO.getTimeTaken());
        dailyReports.setKeyLearnings(dailyReportAddDTO.getKeyLearnings());
        dailyReports.setPlanForTomorrow(dailyReportAddDTO.getPlanForTomorrow());
        dailyReports.setStatus(DailyReports.ReportStatus.valueOf(dailyReportAddDTO.getStatus().name()));
        dailyReports.setCourses(course.get());
        dailyReports.setTrainees(trainee.get());

        // Save DailyReports entity
        dailyReportsRepository.save(dailyReports);

        return dailyReportAddDTO;
    }
    }



