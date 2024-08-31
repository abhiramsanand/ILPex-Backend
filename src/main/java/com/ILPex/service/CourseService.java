package com.ILPex.service;

import com.ILPex.DTO.*;

import java.util.List;

public interface CourseService {
    List<CourseDayBatchDTO> getCoursesByBatchId(Long batchId);
    //    CourseDurationDTO getTotalCourseDuration(Long batchId);
    TotalCourseDaysDTO getTotalCourseDaysCompleted(Long batchId);
    TotalCourseDurationDTO getTotalCourseDuration(Long batchId);
    List<CourseDailyReportDTO> getCourseDetails(Long traineeId, Long batchId);


}
