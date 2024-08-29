package com.ILPex.service;

import com.ILPex.DTO.DayNumberWithDateDTO;
import com.ILPex.entity.Courses;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface CoursesService {
//    public void saveCoursesFromExcel(MultipartFile file, Long batchId);
    void saveCourses(List<Courses> courses);
    List<LocalDate> getAllCourseDates();
    void shiftCourseDates(LocalDate holiday);
    void updateCourseDatesForHoliday(LocalDate holidayDate);
    List<DayNumberWithDateDTO> getAllCourseDatesWithDayNumber();
}
