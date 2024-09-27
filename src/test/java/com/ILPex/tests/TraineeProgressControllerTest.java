package com.ILPex.controller;
import com.ILPex.DTO.CourseProgressDTO;
import com.ILPex.DTO.TraineeActualVsEstimatedDurationDTO;
import com.ILPex.DTO.TraineeCourseDurationDTO;
import com.ILPex.service.TraineeProgressService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TraineeProgressController.class)
public class TraineeProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TraineeProgressService traineeProgressService;

    @Test
    public void givenValidRequest_whenGetLastAccessedDayNumberForTrainees_thenReturnList() throws Exception {
        // Initialize mock data
        Map<String, Object> traineeData1 = new HashMap<>();
        traineeData1.put("traineeId", 1L);
        traineeData1.put("lastAccessedDayNumber", 15);

        Map<String, Object> traineeData2 = new HashMap<>();
        traineeData2.put("traineeId", 2L);
        traineeData2.put("lastAccessedDayNumber", 20);

        List<Map<String, Object>> traineeList = List.of(traineeData1, traineeData2);

        // Mock the service method
        when(traineeProgressService.getLastAccessedDayNumberForTrainees()).thenReturn(traineeList);

        // Perform the request and validate
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ilpex/traineeprogress/trainee/last-accessed-day-number")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].traineeId").value(1))
                .andExpect(jsonPath("$[0].lastAccessedDayNumber").value(15))
                .andExpect(jsonPath("$[1].traineeId").value(2))
                .andExpect(jsonPath("$[1].lastAccessedDayNumber").value(20));
    }

    @Test
    public void givenValidTraineeId_whenGetTraineeProgress_thenReturnListOfCourseProgressDTO() throws Exception {
        // Initialize mock data
        Long traineeId = 1L;
        List<CourseProgressDTO> courseProgressList = Arrays.asList(
                new CourseProgressDTO("Java Basics", 5, 10, 8, 80),
                new CourseProgressDTO("Advanced Java", 10, 20, 15, 75)
        );

        // Mock the service method
        when(traineeProgressService.getTraineeProgress(traineeId))
                .thenReturn(courseProgressList);

        // Perform the request and validate
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ilpex/traineeprogress/{traineeId}", traineeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].courseName").value("Java Basics"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].dayNumber").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].estimatedDuration").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].duration").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].percentageCompleted").value(80))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].courseName").value("Advanced Java"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].dayNumber").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].estimatedDuration").value(20))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].duration").value(15))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].percentageCompleted").value(75));
    }
    @Test
    public void givenValidBatchId_whenGetTotalCourseDurationByBatchId_thenReturnListOfTraineeCourseDurationDTO() throws Exception {
        // Initialize mock data
        Long batchId = 1L;
        List<TraineeCourseDurationDTO> courseDurations = Arrays.asList(
                new TraineeCourseDurationDTO("John Doe", 40L),
                new TraineeCourseDurationDTO("Jane Smith", 35L)
        );

        // Mock the service method
        when(traineeProgressService.findTotalCourseDurationDTOByBatchId(batchId))
                .thenReturn(courseDurations);

        // Perform the request and validate
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ilpex/traineeprogress/course-duration")
                        .param("batchId", batchId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].traineeName").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalCourseDuration").value(40))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].traineeName").value("Jane Smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].totalCourseDuration").value(35));
    }

    @Test
    public void givenValidBatchId_whenGetTotalDurationAndEstimatedDurationByTraineeAndBatch_thenReturnListOfTraineeActualVsEstimatedDurationDTO() throws Exception {
        // Initialize mock data
        Long batchId = 1L;
        List<TraineeActualVsEstimatedDurationDTO> durations = Arrays.asList(
                new TraineeActualVsEstimatedDurationDTO("john_doe", 120L, 100L),
                new TraineeActualVsEstimatedDurationDTO("jane_smith", 90L, 85L)
        );

        // Mock the service method
        when(traineeProgressService.getTotalDurationAndEstimatedDurationByTraineeIdAndBatch(batchId))
                .thenReturn(durations);

        // Perform the request and validate
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ilpex/traineeprogress/duration")
                        .param("batchId", batchId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("john_doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalDuration").value(120))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalEstimatedDuration").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("jane_smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].totalDuration").value(90))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].totalEstimatedDuration").value(85));
    }


}
