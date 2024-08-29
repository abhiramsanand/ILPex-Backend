package com.ILPex.service;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.DTO.CourseDurationDTO;
import com.ILPex.DTO.TotalCourseDaysDTO;
import com.ILPex.DTO.TotalCourseDurationDTO;

import java.util.List;

public interface CourseService {
    List<CourseDayBatchDTO> getCoursesByBatchId(Long batchId);
    //    CourseDurationDTO getTotalCourseDuration(Long batchId);
    TotalCourseDaysDTO getTotalCourseDaysCompleted(Long batchId);
    TotalCourseDurationDTO getTotalCourseDuration(Long batchId);


}
