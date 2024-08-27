package com.ILPex.service;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.entity.Courses;

import java.util.List;

public interface CourseService {
    List<CourseDayBatchDTO> getCoursesByBatchId(Long batchId);
    void saveCourses(List<Courses> courses);
}
