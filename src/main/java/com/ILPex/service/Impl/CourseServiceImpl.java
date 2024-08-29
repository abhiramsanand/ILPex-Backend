package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.DTO.CourseDurationDTO;
import com.ILPex.DTO.TotalCourseDaysDTO;
import com.ILPex.DTO.TotalCourseDurationDTO;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {


    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private BatchRepository batchesRepository;


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
}