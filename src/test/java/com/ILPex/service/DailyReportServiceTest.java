package com.ILPex.service;

import com.ILPex.DTO.DailyReportDTO;
import com.ILPex.DTO.DailyReportDetailsDTO;
import com.ILPex.DTO.DailyReportEditDTO;
import com.ILPex.DTO.DailyReportRequestDTO;
import com.ILPex.entity.Courses;
import com.ILPex.entity.DailyReports;
import com.ILPex.entity.Trainees;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.DailyReportsRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.DailyReportService;
import com.ILPex.service.Impl.DailyReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyReportServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DailyReportsRepository dailyReportsRepository;

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private TraineesRepository traineesRepository;

    @InjectMocks
    private DailyReportServiceImpl dailyReportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void givenCourseDateBatchIdAndTraineeId_whenGetCourseDetailsWithTimeTaken_thenReturnDailyReportDTOList() {
        // Given
        LocalDateTime courseDate = LocalDateTime.now();
        Long batchId = 1L;
        Long traineeId = 1L;

        Courses course = new Courses();
        course.setId(1L);
        course.setCourseName("Java Basics");
        course.setCourseDate(Timestamp.valueOf(courseDate));

        List<Courses> coursesList = Collections.singletonList(course);
        DailyReports dailyReport = new DailyReports();
        dailyReport.setId(1L);
        dailyReport.setTimeTaken(120);

        when(coursesRepository.findByCourseDateAndBatchId(courseDate, batchId)).thenReturn(coursesList);
        when(dailyReportsRepository.findByCourseIdAndTraineeId(course.getId(), traineeId)).thenReturn(Optional.of(dailyReport));

        // When
        List<DailyReportDTO> result = dailyReportService.getCourseDetailsWithTimeTaken(courseDate, batchId, traineeId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getCourseName());
        assertEquals(120, result.get(0).getTimeTaken());
    }

    @Test
    public void givenCourseIdAndTraineeId_whenGetLearningPlanDetails_thenReturnDailyReportDetailsDTO() {
        // Given
        Long courseId = 1L;
        Long traineeId = 1L;

        DailyReports dailyReport = new DailyReports();
        dailyReport.setKeylearnings("Learned Java basics");
        dailyReport.setPlanfortomorrow("Practice more examples");

        when(dailyReportsRepository.findByCourseIdAndTraineeId(courseId, traineeId)).thenReturn(Optional.of(dailyReport));

        // When
        DailyReportDetailsDTO result = dailyReportService.getLearningPlanDetails(courseId, traineeId);

        // Then
        assertNotNull(result);
        assertEquals("Learned Java basics", result.getKeylearnings());
        assertEquals("Practice more examples", result.getPlanfortomorrow());
    }

    @Test
    public void givenInvalidCourseIdAndTraineeId_whenGetLearningPlanDetails_thenReturnNull() {
        // Given
        Long courseId = 1L;
        Long traineeId = 1L;

        when(dailyReportsRepository.findByCourseIdAndTraineeId(courseId, traineeId)).thenReturn(Optional.empty());

        // When
        DailyReportDetailsDTO result = dailyReportService.getLearningPlanDetails(courseId, traineeId);

        // Then
        assertNull(result);
    }

    @Test
    public void givenDailyReportId_whenGetDailyReportEditDetails_thenReturnDailyReportEditDTO() {
        // Given
        Long dailyReportId = 1L;

        Courses course = new Courses();
        course.setCourseName("Java Basics");

        DailyReports dailyReport = new DailyReports();
        dailyReport.setCourses(course);
        dailyReport.setTimeTaken(120);
        dailyReport.setKeylearnings("Learned Java basics");
        dailyReport.setPlanfortomorrow("Practice more examples");

        when(dailyReportsRepository.findById(dailyReportId)).thenReturn(Optional.of(dailyReport));

        // When
        DailyReportEditDTO result = dailyReportService.getDailyReportEditDetails(dailyReportId);

        // Then
        assertNotNull(result);
        assertEquals("Java Basics", result.getCourseName());
        assertEquals(120, result.getTimeTaken());
        assertEquals("Learned Java basics", result.getKeylearnings());
        assertEquals("Practice more examples", result.getPlanfortomorrow());
    }

    @Test
    public void givenInvalidDailyReportId_whenGetDailyReportEditDetails_thenThrowException() {
        // Given
        Long dailyReportId = 1L;

        when(dailyReportsRepository.findById(dailyReportId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dailyReportService.getDailyReportEditDetails(dailyReportId);
        });

        assertEquals("Daily report not found", exception.getMessage());
    }

    @Test
    public void givenValidRequestDTO_whenAddOrUpdateDailyReport_thenReturnUpdatedDailyReports() {
        // Given
        DailyReportRequestDTO dailyReportRequestDTO = new DailyReportRequestDTO();
        dailyReportRequestDTO.setKeyLearnings("Learned Java basics");
        dailyReportRequestDTO.setPlanForTomorrow("Practice more examples");
        dailyReportRequestDTO.setTimeTaken(120);

        Long traineeId = 1L;
        Long courseId = 1L;

        Trainees trainee = new Trainees();
        trainee.setId(traineeId);

        Courses course = new Courses();
        course.setId(courseId);

        DailyReports existingDailyReport = new DailyReports();
        existingDailyReport.setId(1L);
        existingDailyReport.setTrainees(trainee);
        existingDailyReport.setCourses(course);

        when(dailyReportsRepository.findByTrainees_IdAndCourses_Id(traineeId, courseId)).thenReturn(Optional.of(existingDailyReport));
        when(dailyReportsRepository.save(any(DailyReports.class))).thenReturn(existingDailyReport);

        // When
        DailyReports result = dailyReportService.addOrUpdateDailyReport(dailyReportRequestDTO, traineeId, courseId);

        // Then
        assertNotNull(result);
        assertEquals("Learned Java basics", result.getKeylearnings());
        assertEquals("Practice more examples", result.getPlanfortomorrow());
        assertEquals(120, result.getTimeTaken());
    }

    @Test
    public void givenNewDailyReportRequestDTO_whenAddOrUpdateDailyReport_thenReturnNewDailyReports() {
        // Given
        DailyReportRequestDTO dailyReportRequestDTO = new DailyReportRequestDTO();
        dailyReportRequestDTO.setKeyLearnings("Learned Java basics");
        dailyReportRequestDTO.setPlanForTomorrow("Practice more examples");
        dailyReportRequestDTO.setTimeTaken(120);

        Long traineeId = 1L;
        Long courseId = 1L;

        Trainees trainee = new Trainees();
        trainee.setId(traineeId);

        Courses course = new Courses();
        course.setId(courseId);

        when(dailyReportsRepository.findByTrainees_IdAndCourses_Id(traineeId, courseId)).thenReturn(Optional.empty());
        when(traineesRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
        when(coursesRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(dailyReportsRepository.save(any(DailyReports.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DailyReports result = dailyReportService.addOrUpdateDailyReport(dailyReportRequestDTO, traineeId, courseId);

        // Then
        assertNotNull(result);
        assertEquals("Learned Java basics", result.getKeylearnings());
        assertEquals("Practice more examples", result.getPlanfortomorrow());
        assertEquals(120, result.getTimeTaken());
    }
    @Test
    public void givenInvalidTraineeId_whenAddOrUpdateDailyReport_thenThrowException() {
        // Given
        DailyReportRequestDTO dailyReportRequestDTO = new DailyReportRequestDTO();
        Long traineeId = 1L;
        Long courseId = 1L;

        when(dailyReportsRepository.findByTrainees_IdAndCourses_Id(traineeId, courseId)).thenReturn(Optional.empty());
        when(traineesRepository.findById(traineeId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dailyReportService.addOrUpdateDailyReport(dailyReportRequestDTO, traineeId, courseId);
        });

        assertEquals("Trainee not found", exception.getMessage());
    }

    @Test
    public void givenInvalidCourseId_whenAddOrUpdateDailyReport_thenThrowException() {
        // Given
        DailyReportRequestDTO dailyReportRequestDTO = new DailyReportRequestDTO();
        Long traineeId = 1L;
        Long courseId = 1L;

        Trainees trainee = new Trainees();
        trainee.setId(traineeId);

        when(dailyReportsRepository.findByTrainees_IdAndCourses_Id(traineeId, courseId)).thenReturn(Optional.empty());
        when(traineesRepository.findById(traineeId)).thenReturn(Optional.of(trainee));
        when(coursesRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dailyReportService.addOrUpdateDailyReport(dailyReportRequestDTO, traineeId, courseId);
        });

        assertEquals("Course not found", exception.getMessage());
    }

}
