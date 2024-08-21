package com.ILPex.controller;


import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.service.BatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.mockito.Mockito.when;

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
                .andExpect(MockMvcResultMatchers.status().isOk());
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courseName").value("Machine Learning"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dayNumber").value(9))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].batchName").value("Batch 3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courseDuration").value("5 hours"))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));
    }




}
