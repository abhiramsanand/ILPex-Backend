package com.ILPex.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
public class BatchControllerTest {

    @Autowired
    private BatchController batchController;

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
    public void testGetDaywiseCoursesForAllBatches() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/batches/daywise-courses"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // Further assertions can be added to verify the content of the response
    }
}
