package com.ILPex.service;

import com.ILPex.DTO.PendingCourseDTO;

import java.util.List;

public interface PendingCourseService {
    List<PendingCourseDTO> getPendingCoursesForToday(Long traineeId);
}
