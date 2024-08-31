package com.ILPex.service;

import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Courses;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    List<CourseDayBatchDTO> getCoursesByBatchId(Long batchId);
    TotalCourseDaysDTO getTotalCourseDaysCompleted(Long batchId);
    TotalCourseDurationDTO getTotalCourseDuration(Long batchId);
    void saveCourses(List<Courses> coursesList);
    List<Courses> parseCourseExcelFile(MultipartFile file, Batches batch) throws IOException;
    List<CourseDailyReportDTO> getCourseDetails(Long traineeId, Long batchId);
    List<PendingSubmissionDTO> getPendingSubmissions(Long batchId, Long traineeId);
}
