package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.Courses;
import com.ILPex.entity.DailyReports;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.DailyReportsRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {


    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private BatchRepository batchesRepository;

    @Autowired
    private DailyReportsRepository dailyReportsRepository;

    @Override
    public List<CourseDayBatchDTO> getCoursesByBatchId(Long batchId) {
        return coursesRepository.findCoursesByBatchId(batchId);
    }


//    @Override
//    public CourseDurationDTO getTotalCourseDuration(Long batchId) {
//        Long totalDurationMinutes = coursesRepository.findTotalCourseDurationByBatchId(batchId);
//        return new CourseDurationDTO(totalDurationMinutes != null ? totalDurationMinutes : 0);
//    }

    @Override
    public TotalCourseDaysDTO getTotalCourseDaysCompleted(Long batchId) {
        Long totalDays = coursesRepository.countDistinctCourseDaysCompletedByBatchId(batchId);
        return new TotalCourseDaysDTO(totalDays);
    }

    @Override
    public TotalCourseDurationDTO getTotalCourseDuration(Long batchId) {
        // Fetch the day_number for the specified batch
        Integer dayNumber = batchesRepository.findDayNumberById(batchId);

        if (dayNumber == null) {
            throw new RuntimeException("Batch not found with ID: " + batchId);
        }

        // Fetch the total course duration up to the day number
        Long totalDuration = coursesRepository.getTotalCourseDurationUpToDayNumber(dayNumber);

        return new TotalCourseDurationDTO(totalDuration);
    }

    @Override
    public List<CourseDailyReportDTO> getCourseDetails(Long traineeId, Long batchId) {
        List<CourseDailyReportDTO> responseList = new ArrayList<>();
        Date currentDate = new Date(); // Current date

        // Fetch courses for the given batchId where course_date is before current date
        List<Courses> courses = coursesRepository.findByBatchIdAndCourseDateBefore(batchId, currentDate);

        // Iterate over each course to find corresponding daily report
        for (Courses course : courses) {
            CourseDailyReportDTO dto = new CourseDailyReportDTO();
            dto.setDayNumber(course.getDayNumber());
            dto.setCourseName(course.getCourseName());

            // Fetch daily report based on traineeId and courseId
            Optional<DailyReports> dailyReportOpt = dailyReportsRepository.findByTrainees_IdAndCourses_Id(traineeId , course.getId());
            if (dailyReportOpt.isPresent()) {
                DailyReports dailyReport = dailyReportOpt.get();
                dto.setTimeTaken(dailyReport.getTimeTaken());
                dto.setDailyReportId(dailyReport.getId());
            } else {
                dto.setTimeTaken(0); // No entry means time taken is 0
                dto.setDailyReportId(null); // No daily report found
            }
            responseList.add(dto);
        }

        return responseList;
    }

}