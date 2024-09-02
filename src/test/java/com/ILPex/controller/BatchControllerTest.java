package com.ILPex.controller;


import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.service.BatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

import static java.nio.file.Paths.get;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
public class BatchControllerTest {

    @Autowired
    private BatchController batchController;

    @MockBean
    private BatchService batchService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(batchController).build();
    }

    @Test
    public void testGetBatches() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/batches"))
                .andExpect(status().isOk());
        // Further assertions can be added to verify the content of the response
    }


    @Test
    @DisplayName("Test getDaywiseCoursesForAllBatches - success")
    void givenValidRequest_whenGetDaywiseCoursesForAllBatches_thenReturnsCourseList() throws Exception {
        // Given: Mocked data and service behavior
        CourseDayBatchDTO courseDayBatchDTO = new CourseDayBatchDTO(); // Initialize with test data
        courseDayBatchDTO.setCourseName("Machine Learning");
        courseDayBatchDTO.setDayNumber(9);
        courseDayBatchDTO.setBatchName("Batch 3");
        courseDayBatchDTO.setCourseDuration("5 hours");

        // Mock the service to return the course list
        when(batchService.getDaywiseCoursesForAllBatches()).thenReturn(Arrays.asList(courseDayBatchDTO));

        // When: Simulate the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/batches/daywise-courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courseName").value("Machine Learning"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dayNumber").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].batchName").value("Batch 3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courseDuration").value("5 hours"))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));
    }

    @Test
    public void givenBatchId_whenBatchDetailsAreFound_thenReturnBatchDetails() throws Exception {
        Long batchId = 1L;

        // Prepare mock data
        TraineeDisplayByBatchDTO trainee1 = new TraineeDisplayByBatchDTO(1L, "John Doe", "john.doe@example.com", "john.doe@percipio.com", "password123");
        BatchDetailsDTO batchDetailsDTO = new BatchDetailsDTO(
                batchId, "Batch A", "Program X",true, "2024-01-01", "2024-12-31", 20, Arrays.asList(trainee1)
        );

        // Mock the service method
        when(batchService.getBatchDetails(batchId)).thenReturn(batchDetailsDTO);

        // Perform the GET request and verify the status and response
        mockMvc.perform(get("/api/v1/batches/{batchId}/details", batchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.batchId").value(batchId))
                .andExpect(jsonPath("$.batchName").value("Batch A"))
                .andExpect(jsonPath("$.programName").value("Program X"))// Ensure this matches the DTO
                .andExpect(jsonPath("$.startDate").value("2024-01-01"))
                .andExpect(jsonPath("$.endDate").value("2024-12-31"))
                .andExpect(jsonPath("$.numberOfTrainees").value(20))
                .andExpect(jsonPath("$.trainees[0].traineeId").value(1L))
                .andExpect(jsonPath("$.trainees[0].userName").value("John Doe"))
                .andExpect(jsonPath("$.trainees[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.trainees[0].percipioEmail").value("john.doe@percipio.com"))
                .andExpect(jsonPath("$.trainees[0].password").value("password123"));
    }

    @Test
    public void givenValidBatchDataAndFile_whenCreateBatch_thenReturnBatchId() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Initialize mock data
        Long batchId = 1L;
        BatchCreationDTO batchCreationDTO = new BatchCreationDTO();
        batchCreationDTO.setBatchName("Batch 1");
        batchCreationDTO.setProgramId("program123");
        batchCreationDTO.setProgramName("Program Name");
        batchCreationDTO.setStartDate(Timestamp.valueOf("2024-08-01 00:00:00"));
        batchCreationDTO.setEndDate(Timestamp.valueOf("2024-08-31 23:59:59"));
        batchCreationDTO.setIsActive(true);

        // Mock file
        MockMultipartFile file = new MockMultipartFile("file", "filename.xlsx", "application/vnd.ms-excel", "test content".getBytes());

        // Convert DTO to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String batchData = objectMapper.writeValueAsString(batchCreationDTO);

        // Create a mock Batches object
        Batches batch = new Batches();
        batch.setId(batchId);

        // Mock the service method
        when(batchService.createBatchWithTrainees(eq(batchCreationDTO), any(MultipartFile.class)))
                .thenReturn(batch);

        // Perform the request and validate
        mockMvc.perform(multipart("/api/v1/batches/create")
                        .file(file)
                        .param("batchData", batchData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(batchId.toString()));
    }

    @Test
    public void givenInvalidBatchData_whenCreateBatch_thenReturnBadRequest() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Initialize invalid mock data
        BatchCreationDTO batchCreationDTO = new BatchCreationDTO();
        batchCreationDTO.setBatchName(""); // Empty batch name or other invalid data
        batchCreationDTO.setProgramId(null); // Invalid program ID
        batchCreationDTO.setStartDate(null); // Null start date
        batchCreationDTO.setEndDate(Timestamp.valueOf("2024-08-31 23:59:59"));
        batchCreationDTO.setIsActive(true);

        // Mock file
        MockMultipartFile file = new MockMultipartFile("file", "filename.xlsx", "application/vnd.ms-excel", "test content".getBytes());

        // Convert DTO to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String batchData = objectMapper.writeValueAsString(batchCreationDTO);

        // Mock the service method to throw an IllegalArgumentException
        when(batchService.createBatchWithTrainees(eq(batchCreationDTO), any(MultipartFile.class)))
                .thenThrow(new IllegalArgumentException("Invalid batch data"));

        // Perform the request and validate
        mockMvc.perform(multipart("/api/v1/batches/create")
                        .file(file)
                        .param("batchData", batchData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid batch data"));
    }


}