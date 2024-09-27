package com.ILPex.controller;


import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Courses;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.service.BatchService;
import com.ILPex.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class CourseControllerTest {


    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private BatchService batchService;

    @Autowired
    private CourseController courseController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }


    @Test
    public void givenBatchId_whenGetCoursesByBatchId_thenReturnCoursesList() throws Exception {
        Long batchId = 1L;

        // Mock data for CourseDayBatchDTO
        CourseDayBatchDTO course1 = new CourseDayBatchDTO();
        course1.setCourseName("Java Basics");
        course1.setDayNumber(1);
        course1.setBatchName("Batch A");
        course1.setCourseDuration("4 weeks");

        CourseDayBatchDTO course2 = new CourseDayBatchDTO();
        course2.setCourseName("Advanced Java");
        course2.setDayNumber(8);
        course2.setBatchName("Batch A");
        course2.setCourseDuration("6 weeks");

        List<CourseDayBatchDTO> courseDayBatchDTOs = List.of(course1, course2);

        when(courseService.getCoursesByBatchId(batchId)).thenReturn(courseDayBatchDTOs);

        mockMvc.perform(get("/api/courses/batch/{batchId}", batchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].courseName").value("Java Basics"))
                .andExpect(jsonPath("$[0].dayNumber").value(1))
                .andExpect(jsonPath("$[0].batchName").value("Batch A"))
                .andExpect(jsonPath("$[0].courseDuration").value("4 weeks"))
                .andExpect(jsonPath("$[1].courseName").value("Advanced Java"))
                .andExpect(jsonPath("$[1].dayNumber").value(8))
                .andExpect(jsonPath("$[1].batchName").value("Batch A"))
                .andExpect(jsonPath("$[1].courseDuration").value("6 weeks"));
    }

    @Test
    public void givenBatchId_whenGetTotalCourseDaysCompleted_thenReturnTotalCourseDaysDTO() throws Exception {
        Long batchId = 1L;
        TotalCourseDaysDTO totalCourseDaysDTO = new TotalCourseDaysDTO();
        totalCourseDaysDTO.setTotalCourseDaysCompleted(30L); // Set mock data as needed

        when(courseService.getTotalCourseDaysCompleted(batchId)).thenReturn(totalCourseDaysDTO);

        mockMvc.perform(get("/api/courses/total-course-days-completed/{batchId}", batchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalCourseDaysCompleted").value(30)); // Adjust according to actual DTO fields
    }


    @Test
    public void givenBatchId_whenGetTotalCourseDuration_thenReturnTotalCourseDurationDTO() throws Exception {
        Long batchId = 1L;
        TotalCourseDurationDTO totalCourseDurationDTO = new TotalCourseDurationDTO();
        totalCourseDurationDTO.setTotalDurationMinutes(30L); // Set mock data as needed

        when(courseService.getTotalCourseDuration(batchId)).thenReturn(totalCourseDurationDTO);

        mockMvc.perform(get("/api/courses/total-duration/{batchId}", batchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalDurationMinutes").value(30L)); // Ensure this matches the value set in the mock DTO
    }

    @Test
    public void givenValidBatchIdAndFile_whenCreateCourses_thenReturnSuccessMessage() throws Exception {
        Long batchId = 1L;
        MultipartFile mockFile = new MockMultipartFile("file", "courses.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Mock file content".getBytes());

        Batches mockBatch = new Batches();
        mockBatch.setId(batchId); // Assuming your Batches entity has a setBatchId method or similar

        when(batchService.getBatchById(batchId)).thenReturn(mockBatch);
        when(courseService.parseCourseExcelFile(any(MultipartFile.class), eq(mockBatch))).thenReturn(Collections.singletonList(new Courses()));

        mockMvc.perform(multipart("/api/courses/create")
                        .file((MockMultipartFile) mockFile)
                        .param("batchId", String.valueOf(batchId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Courses created successfully"));
    }
    @Test
    public void givenInvalidBatchId_whenCreateCourses_thenReturnNotFoundMessage() throws Exception {
        Long batchId = 1L;
        MultipartFile mockFile = new MockMultipartFile("file", "courses.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Mock file content".getBytes());

        when(batchService.getBatchById(batchId)).thenReturn(null);

        mockMvc.perform(multipart("/api/courses/create")
                        .file((MockMultipartFile) mockFile)
                        .param("batchId", String.valueOf(batchId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Batch not found with ID " + batchId));
    }
    @Test
    public void givenValidBatchIdAndFile_whenCreateCourses_thenReturnErrorMessageOnIOException() throws Exception {
        Long batchId = 1L;
        MultipartFile mockFile = new MockMultipartFile("file", "courses.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Mock file content".getBytes());

        Batches mockBatch = new Batches();
        mockBatch.setId(batchId); // Assuming your Batches entity has a setBatchId method or similar

        when(batchService.getBatchById(batchId)).thenReturn(mockBatch);
        when(courseService.parseCourseExcelFile(any(MultipartFile.class), eq(mockBatch))).thenThrow(new IOException("Mock IOException"));

        mockMvc.perform(multipart("/api/courses/create")
                        .file((MockMultipartFile) mockFile)
                        .param("batchId", String.valueOf(batchId)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing Excel file"));
    }

    @Test
    public void givenTraineeIdAndBatchId_whenGetCourseDetails_thenReturnCourseDailyReportDTOList() throws Exception {
        Long traineeId = 1L;
        Long batchId = 1L;

        // Mocking a list of CourseDailyReportDTO with some data
        CourseDailyReportDTO courseDetail1 = new CourseDailyReportDTO();
        courseDetail1.setDayNumber(1);
        courseDetail1.setCourseName("Course A");
        courseDetail1.setTimeTaken(120); // Time taken in minutes
        courseDetail1.setDailyReportId(101L);

        CourseDailyReportDTO courseDetail2 = new CourseDailyReportDTO();
        courseDetail2.setDayNumber(2);
        courseDetail2.setCourseName("Course B");
        courseDetail2.setTimeTaken(90); // Time taken in minutes
        courseDetail2.setDailyReportId(102L);

        List<CourseDailyReportDTO> courseDetailsList = Arrays.asList(courseDetail1, courseDetail2);

        // Mocking the service method to return the mocked list
        when(courseService.getCourseDetails(traineeId, batchId)).thenReturn(courseDetailsList);

        // Performing the GET request and verifying the response
        mockMvc.perform(get("/api/courses/WholeReport/{traineeId}/batch/{batchId}", traineeId, batchId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].dayNumber").value(1))
                .andExpect(jsonPath("$[0].courseName").value("Course A"))
                .andExpect(jsonPath("$[0].timeTaken").value(120))
                .andExpect(jsonPath("$[0].dailyReportId").value(101))
                .andExpect(jsonPath("$[1].dayNumber").value(2))
                .andExpect(jsonPath("$[1].courseName").value("Course B"))
                .andExpect(jsonPath("$[1].timeTaken").value(90))
                .andExpect(jsonPath("$[1].dailyReportId").value(102));
    }

    @Test
    public void givenInvalidBatchId_whenGetCourseDetails_thenReturnEmptyList() throws Exception {
        Long traineeId = 1L;
        Long invalidBatchId = -1L; // Assuming -1 is an invalid batch ID

        // Mocking the service method to return an empty list for an invalid batch ID
        when(courseService.getCourseDetails(traineeId, invalidBatchId)).thenReturn(Collections.emptyList());

        // Performing the GET request and verifying the response
        mockMvc.perform(get("/api/courses/WholeReport/{traineeId}/batch/{batchId}", traineeId, invalidBatchId))
                .andExpect(status().isOk()) // Ensure the status is OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Verify content type is JSON
                .andExpect(jsonPath("$").isEmpty()); // Ensure that the returned list is empty
    }


    @Test
    public void givenBatchIdAndTraineeId_whenGetPendingSubmissions_thenReturnPendingSubmissionDTOList() throws Exception {
        Long batchId = 1L;
        Long traineeId = 1L;

        // Creating mock data for PendingSubmissionDTO
        PendingSubmissionDTO submission1 = new PendingSubmissionDTO();
        submission1.setCourseId(1L);
        submission1.setCourseName("Course A");
        submission1.setDayNumber(10);

        PendingSubmissionDTO submission2 = new PendingSubmissionDTO();
        submission2.setCourseId(2L);
        submission2.setCourseName("Course B");
        submission2.setDayNumber(20);

        List<PendingSubmissionDTO> pendingSubmissionsList = Arrays.asList(submission1, submission2);

        // Mocking the service method to return the mocked list
        when(courseService.getPendingSubmissions(batchId, traineeId)).thenReturn(pendingSubmissionsList);

        // Performing the GET request and verifying the response
        mockMvc.perform(get("/api/courses/pending-submissions")
                        .param("batchId", String.valueOf(batchId))
                        .param("traineeId", String.valueOf(traineeId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].courseId").value(1))
                .andExpect(jsonPath("$[0].courseName").value("Course A"))
                .andExpect(jsonPath("$[0].dayNumber").value(10))
                .andExpect(jsonPath("$[1].courseId").value(2))
                .andExpect(jsonPath("$[1].courseName").value("Course B"))
                .andExpect(jsonPath("$[1].dayNumber").value(20));
    }

    @Test
    public void givenInvalidBatchIdOrTraineeId_whenGetPendingSubmissions_thenReturnEmptyList() throws Exception {
        Long invalidBatchId = 999L;
        Long invalidTraineeId = 999L;

        // Mocking the service method to return an empty list for invalid IDs
        when(courseService.getPendingSubmissions(invalidBatchId, invalidTraineeId)).thenReturn(Collections.emptyList());

        // Performing the GET request and verifying the response
        mockMvc.perform(get("/api/courses/pending-submissions")
                        .param("batchId", String.valueOf(invalidBatchId))
                        .param("traineeId", String.valueOf(invalidTraineeId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty()); // Ensures the returned list is empty
    }

    @Test
    public void givenValidHolidayDate_whenUnmarkHoliday_thenReturnSuccessMessage() throws Exception {
        // Given: Setup the input data
        Map<String, String> payload = new HashMap<>();
        payload.put("holidayDate", "2023-09-01");

        // When: Define the behavior of the mocked service
        doNothing().when(courseService).restoreCourseDatesForWorkingDay(any(LocalDate.class));

        // Then: Perform the POST request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/courses/unmark-holiday")
                        .contentType(MediaType.APPLICATION_JSON)  // Set the correct content type
                        .content(new ObjectMapper().writeValueAsString(payload)))  // Pass JSON payload as string
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Holiday unmarked and course dates updated successfully."));

        // Verify: Ensure the service method was called with the correct arguments
        verify(courseService).restoreCourseDatesForWorkingDay(LocalDate.of(2023, 9, 1));
    }

    @Test
    public void givenHolidayDateAndDescription_whenMarkHolidayDay_thenReturnSuccessMessage() throws Exception {
        // Given: Setup the input data
        Map<String, String> payload = new HashMap<>();
        payload.put("holidayDate", "2023-09-01");
        payload.put("description", "Labor Day");

        // When: Define the behavior of the mocked service
        doNothing().when(courseService).updateCourseDatesForHoliday(any(LocalDate.class), any(String.class));

        // Then: Perform the POST request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/courses/mark-holiday/day")
                        .contentType(MediaType.APPLICATION_JSON)  // Set the correct content type
                        .content(new ObjectMapper().writeValueAsString(payload)))  // Pass JSON payload as string
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Holiday marked and course dates updated successfully."));

        // Verify: Ensure the service method was called with the correct arguments
        verify(courseService).updateCourseDatesForHoliday(LocalDate.of(2023, 9, 1), "Labor Day");
    }





}