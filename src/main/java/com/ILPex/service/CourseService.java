package com.ILPex.service;

import com.ILPex.DTO.CourseDayBatchDTO;

import java.util.List;

public interface CourseService {
    List<CourseDayBatchDTO> getCoursesByBatchId(Long batchId);
}
