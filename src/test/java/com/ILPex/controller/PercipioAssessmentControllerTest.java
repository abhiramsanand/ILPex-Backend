package com.ILPex.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import com.ILPex.service.PercipioAssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@WebMvcTest(PercipioAssessmentController.class)
@EnableWebMvc
public class PercipioAssessmentControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private PercipioAssessmentService percipioAssessmentService;

    @InjectMocks
    private PercipioAssessmentController percipioAssessmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(percipioAssessmentController).build();
    }


    @Test
    void givenValidRequest_whenGetAverageScoresForAllTraineesWithName_thenReturnAverageScoresWithName() throws Exception {
        // Given
        Map<String, Double> mockResponse = new HashMap<>();
        mockResponse.put("Trainee1", 85.5);
        mockResponse.put("Trainee2", 90.0);
        given(percipioAssessmentService.getAverageScoresForAllTraineesWithName()).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/assessments/averageScore")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Trainee1").value(85.5))
                .andExpect(jsonPath("$.Trainee2").value(90.0));
    }
}
