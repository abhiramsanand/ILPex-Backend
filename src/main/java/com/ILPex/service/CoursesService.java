package com.ILPex.service;

import com.ILPex.entity.Courses;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CoursesService {
//    public void saveCoursesFromExcel(MultipartFile file, Long batchId);
    void saveCourses(List<Courses> courses);
}
