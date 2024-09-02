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
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
        BatchDetailsDTO batchDetailsDTO = new BatchDetailsDTO(); // Initialize with mock data
        batchDetailsDTO.setBatchId(batchId);

        when(batchService.getBatchDetails(batchId)).thenReturn(batchDetailsDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/batches/{batchId}/details", batchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.batchId").value(batchId));
    }


    @Test
    public void givenBatchId_whenBatchNotFound_thenReturnNotFound() throws Exception {
        Long batchId = 1L;

        when(batchService.getBatchDetails(batchId)).thenThrow(new ResourceNotFoundException("Batch not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/batches/{batchId}/details", batchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void givenBatchId_whenServiceThrowsException_thenReturnInternalServerError() throws Exception {
        Long batchId = 1L;

        when(batchService.getBatchDetails(batchId)).thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/batches/{batchId}/details", batchId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void givenTraineeIdAndUpdateDTO_whenUpdateTrainee_thenReturnUpdatedTrainee() throws Exception {
        Long traineeId = 1L;
        TraineeUpdateDTO traineeUpdateDTO = new TraineeUpdateDTO(); // Initialize with mock data
        TraineeDisplayByBatchDTO updatedTraineeDTO = new TraineeDisplayByBatchDTO(); // Initialize with mock data
        updatedTraineeDTO.setTraineeId(traineeId);

        when(batchService.updateTrainee(eq(traineeId), any(TraineeUpdateDTO.class)))
                .thenReturn(updatedTraineeDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/batches/trainees/{traineeId}", traineeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"someField\": \"someValue\" }")) // Replace with actual JSON content
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.traineeId").value(traineeId));
    }
    @Test
    public void givenTraineeId_whenUpdateTraineeAndResourceNotFound_thenReturnNotFound() throws Exception {
        Long traineeId = 1L;
        TraineeUpdateDTO traineeUpdateDTO = new TraineeUpdateDTO(); // Initialize with mock data

        when(batchService.updateTrainee(eq(traineeId), any(TraineeUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Trainee not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/batches/trainees/{traineeId}", traineeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"someField\": \"someValue\" }")) // Replace with actual JSON content
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenTraineeId_whenUpdateTraineeAndInternalServerError_thenReturnInternalServerError() throws Exception {
        Long traineeId = 1L;
        TraineeUpdateDTO traineeUpdateDTO = new TraineeUpdateDTO(); // Initialize with mock data

        when(batchService.updateTrainee(eq(traineeId), any(TraineeUpdateDTO.class)))
                .thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/batches/trainees/{traineeId}", traineeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"someField\": \"someValue\" }")) // Replace with actual JSON content
                .andExpect(status().isInternalServerError());
    }
    @Test
    public void givenValidBatchDataAndFile_whenCreateBatch_thenReturnBatchId() throws Exception {
        // Initialize mock data
        Long batchId = 1L;
        BatchCreationDTO batchCreationDTO = new BatchCreationDTO(); // Initialize with mock data
        MockMultipartFile file = new MockMultipartFile("file", "filename.xlsx", "application/vnd.ms-excel", "test content".getBytes());
        ObjectMapper objectMapper = new ObjectMapper();
        String batchData = objectMapper.writeValueAsString(batchCreationDTO);

        // Create a Batches object and set the batch ID
        Batches batch = new Batches();
        batch.setId(batchId);

        when(batchService.createBatchWithTrainees(eq(batchCreationDTO), any(MultipartFile.class)))
                .thenReturn(batch);

        mockMvc.perform(multipart("/api/v1/batches/create")
                        .file(file)
                        .param("batchData", batchData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(batchId.toString()));
    }

    @Test
    public void givenInvalidBatchData_whenCreateBatch_thenReturnBadRequest() throws Exception {
        String invalidBatchData = "{ invalid json }";
        MockMultipartFile file = new MockMultipartFile("file", "filename.xlsx", "application/vnd.ms-excel", "test content".getBytes());

        mockMvc.perform(multipart("/api/v1/batches/create")
                        .file(file)
                        .param("batchData", invalidBatchData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid batch data format."));
    }

    @Test
    public void givenValidBatchDataAndFile_whenCreateBatch_thenReturnErrorProcessingFile() throws Exception {
        BatchCreationDTO batchCreationDTO = new BatchCreationDTO(); // Initialize with mock data
        MockMultipartFile file = new MockMultipartFile("file", "filename.xlsx", "application/vnd.ms-excel", "test content".getBytes());
        ObjectMapper objectMapper = new ObjectMapper();
        String batchData = objectMapper.writeValueAsString(batchCreationDTO);

        when(batchService.createBatchWithTrainees(eq(batchCreationDTO), any(MultipartFile.class)))
                .thenThrow(new IOException("Error processing the file."));

        mockMvc.perform(multipart("/api/v1/batches/create")
                        .file(file)
                        .param("batchData", batchData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing the file."));
    }
    @Test
    public void givenValidBatchData_whenCreateBatch_thenReturnBadRequestForIllegalArgument() throws Exception {
        BatchCreationDTO batchCreationDTO = new BatchCreationDTO(); // Initialize with mock data
        MockMultipartFile file = new MockMultipartFile("file", "filename.xlsx", "application/vnd.ms-excel", "test content".getBytes());
        ObjectMapper objectMapper = new ObjectMapper();
        String batchData = objectMapper.writeValueAsString(batchCreationDTO);

        when(batchService.createBatchWithTrainees(eq(batchCreationDTO), any(MultipartFile.class)))
                .thenThrow(new IllegalArgumentException("Invalid batch data"));

        mockMvc.perform(multipart("/api/v1/batches/create")
                        .file(file)
                        .param("batchData", batchData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid batch data"));
    }




}
