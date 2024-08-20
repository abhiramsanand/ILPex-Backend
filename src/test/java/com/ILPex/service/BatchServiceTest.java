package com.ILPex.service;

import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.entity.Batches;
import com.ILPex.repository.BatchRepository;
import com.ILPex.service.BatchService;
import com.ILPex.service.Impl.BatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BatchServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BatchRepository batchRepository;

    @InjectMocks
    private BatchServiceImpl batchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenBatchesInRepository_whenGetBatches_thenReturnBatchDTOList() {
        Batches batch = new Batches(); // Initialize with test data
        BatchDTO batchDTO = new BatchDTO(); // Initialize with test data

        // Mock the repository and mapper
        when(batchRepository.findAll()).thenReturn(Arrays.asList(batch));
        when(modelMapper.map(batch, BatchDTO.class)).thenReturn(batchDTO);

        List<BatchDTO> batchDTOs = batchService.getBatches();

        assertEquals(1, batchDTOs.size());
        assertEquals(batchDTO, batchDTOs.get(0));
    }

    @Test
    public void givenNoBatchesInRepository_whenGetBatches_thenReturnEmptyList() {
        // Mock the repository to return an empty list
        when(batchRepository.findAll()).thenReturn(Arrays.asList());

        List<BatchDTO> batchDTOs = batchService.getBatches();

        // Assert that the returned list is empty
        assertEquals(0, batchDTOs.size());
    }
}
