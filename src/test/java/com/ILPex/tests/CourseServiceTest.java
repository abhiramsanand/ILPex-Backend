package com.ILPex.service;

import com.ILPex.entity.Courses;
import com.ILPex.entity.Holiday;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.HolidayRepository;
import com.ILPex.service.CourseServiceTest;
import com.ILPex.service.Impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenHolidayDate_whenUpdateCourseDatesForHoliday_thenUpdateCoursesAndSaveHolidays() {
        LocalDate holidayDate = LocalDate.of(2023, 9, 1);
        Timestamp holidayTimestamp = Timestamp.valueOf(holidayDate.atStartOfDay());

        // Mock Holiday and Courses data
        Holiday holiday = new Holiday();
        holiday.setDate(holidayDate);
        holiday.setDescription("Labor Day");

        Courses course = new Courses();
        course.setDayNumber(1);
        course.setCourseDate(holidayTimestamp);

        when(coursesRepository.findAllByOrderByDayNumberAscCourseDateAsc()).thenReturn(Collections.singletonList(course));
        when(holidayRepository.existsById(eq(holidayDate))).thenReturn(false);  // No existing holiday

        // Call the method under test
        courseService.updateCourseDatesForHoliday(holidayDate, "Labor Day");

        // Verify interactions
        verify(holidayRepository, times(1)).save(any(Holiday.class));  // Save new holiday
        verify(coursesRepository, times(1)).saveAll(anyList());  // Save updated courses
    }
}
