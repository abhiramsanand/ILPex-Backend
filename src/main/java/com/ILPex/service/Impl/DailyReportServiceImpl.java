package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.Courses;
import com.ILPex.entity.DailyReports;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.DailyReportsRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.DailyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public List<DailyReportDTO> getCourseDetailsWithTimeTaken(LocalDateTime courseDate, Long batchId, Long traineeId) {
        List<DailyReportDTO> reportDTOs = new ArrayList<>();
        List<Courses> courses = coursesRepository.findByCourseDateAndBatchId(courseDate, batchId);

        for (Courses course : courses) {
            Long courseId = course.getId();
            String courseName = course.getCourseName();
            Date date=course.getCourseDate();

            Optional<DailyReports> dailyReportOpt = dailyReportsRepository.findByCourseIdAndTraineeId(courseId, traineeId);
            Long id = dailyReportOpt.map(DailyReports::getId).orElse(null);
            Integer timeTaken = dailyReportOpt.map(DailyReports::getTimeTaken).orElse(0);
            DailyReportDTO reportDTO = new DailyReportDTO(id,courseId, date,courseName, timeTaken);
            reportDTOs.add(reportDTO);
        }

        return reportDTOs;
    }
    @Override
    public DailyReportDetailsDTO getLearningPlanDetails(Long courseId, Long traineeId) {
        Optional<DailyReports> dailyReportOpt = dailyReportsRepository.findByCourseIdAndTraineeId(courseId, traineeId);

        if (dailyReportOpt.isPresent()) {
            DailyReports dailyReport = dailyReportOpt.get();
            return new DailyReportDetailsDTO(dailyReport.getKeylearnings(), dailyReport.getPlanfortomorrow());
        } else {
            return null;
        }
    }
    @Override
    public DailyReportEditDTO getDailyReportEditDetails(Long dailyReportId) {
        DailyReports dailyReport = dailyReportsRepository.findById(dailyReportId)
                .orElseThrow(() -> new RuntimeException("Daily report not found"));
        Courses course = dailyReport.getCourses();

        String courseName = course.getCourseName();
        Integer timeTaken = dailyReport.getTimeTaken();
        String keyLearnings = dailyReport.getKeylearnings();
        String planForTomorrow = dailyReport.getPlanfortomorrow();

        return new DailyReportEditDTO(courseName, timeTaken, keyLearnings, planForTomorrow);
    }



    @Override
    public DailyReports addOrUpdateDailyReport(DailyReportRequestDTO dailyReportRequestDTO, Long traineeId, Long courseId) {
        Optional<DailyReports> existingReportOpt = dailyReportsRepository.findByTrainees_IdAndCourses_Id(traineeId, courseId);

        DailyReports dailyReports;

        if (existingReportOpt.isPresent()) {
            dailyReports = existingReportOpt.get();
            // Update the existing report with new values
            dailyReports.setKeylearnings(dailyReportRequestDTO.getKeyLearnings());
            dailyReports.setPlanfortomorrow(dailyReportRequestDTO.getPlanForTomorrow());
            dailyReports.setTimeTaken(dailyReportRequestDTO.getTimeTaken());
            // If needed, set or update the status here
            // dailyReports.setStatus(DailyReports.ReportStatus.SUBMITTED); // Uncomment if needed
        } else {
            dailyReports = new DailyReports();
            dailyReports.setTrainees(traineesRepository.findById(traineeId)
                    .orElseThrow(() -> new RuntimeException("Trainee not found")));
            dailyReports.setCourses(coursesRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found")));
            dailyReports.setKeylearnings(dailyReportRequestDTO.getKeyLearnings());
            dailyReports.setPlanfortomorrow(dailyReportRequestDTO.getPlanForTomorrow());
            dailyReports.setTimeTaken(dailyReportRequestDTO.getTimeTaken());
          //  dailyReports.setStatus(DailyReports.ReportStatus.SUBMITTED); // Ensure this matches your enum type
            dailyReports.setDate(new Date()); // Ensure this is the desired date
        }

        // Save the report to the database
        return dailyReportsRepository.save(dailyReports);
    }


//    @Override
//    public List<DailyReportDTO> getDailyReportByBatchIdDateAndTraineeId(Long batchId, LocalDate courseDate, Long traineeId) {
//        List<DailyReports> reports = dailyReportsRepository.findByCourses_BatchIdAndCourseDateAndTrainees_Id(batchId, courseDate, traineeId);
//
//        return reports.stream()
//                .map(report -> {
//                    DailyReportDTO dto = modelMapper.map(report, DailyReportDTO.class);
//                    dto.setCourseName(coursesRepository.findById(report.getCourses().getId())
//                            .map(Courses::getCourseName)
//                            .orElse("Unknown Course"));
//                    dto.setTimeTaken(report.getTimeTaken() != null ? report.getTimeTaken() : Integer);
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }


//    @Override
//    public List<DailyReportCreationDTO> getDailyReportById(Long id) {
//        DailyReports dailyReport = dailyReportsRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Report not found"));
//
//        DailyReportCreationDTO dailyReportCreationDTO = modelMapper.map(dailyReport, DailyReportCreationDTO.class);
//
//        dailyReportCreationDTO.setCourseName(dailyReport.getCourses().getCourseName());
//        dailyReportCreationDTO.setCourseId(dailyReport.getCourses().getId());
//
//        return List.of(dailyReportCreationDTO);
//    }
//
//    @Override
//    public DailyReportAddDTO addDailyReport(DailyReportAddDTO dailyReportAddDTO) {
//        Optional<Courses> course = coursesRepository.findById(dailyReportAddDTO.getCourseId());
//        if (course.isEmpty()) {
//            throw new RuntimeException("Course not found with id: " + dailyReportAddDTO.getCourseId());
//        }
//
//        Optional<Trainees> trainee = traineesRepository.findById(dailyReportAddDTO.getTraineeId());
//        if (trainee.isEmpty()) {
//            throw new RuntimeException("Trainee not found with id: " + dailyReportAddDTO.getTraineeId());
//        }
//
//        DailyReports dailyReports = new DailyReports();
//        dailyReports.setDate(LocalDateTime.now()); // Set the current date
//        dailyReports.setTimeTaken(dailyReportAddDTO.getTimeTaken());
//        dailyReports.setKeylearnings(dailyReportAddDTO.getKeyLearnings());
//        dailyReports.setPlanfortomorrow(dailyReportAddDTO.getPlanForTomorrow());
//        dailyReports.setStatus(DailyReports.ReportStatus.SUBMITTED); // Set status to "Completed"
//        dailyReports.setCourses(course.get());
//        dailyReports.setTrainees(trainee.get());
//
//        dailyReportsRepository.save(dailyReports);
//
//        return dailyReportAddDTO;
//    }

    }



