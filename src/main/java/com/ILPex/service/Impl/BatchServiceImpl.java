package com.ILPex.service.Impl;

import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.entity.Batches;
import com.ILPex.repository.BatchRepository;
import com.ILPex.service.BatchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements BatchService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BatchRepository batchRepository;

    @Override
    public List<BatchDTO> getBatches() {
        List<Batches> batchList = batchRepository.findAll();
        return batchList.stream().map(batch -> {
            return modelMapper.map(batch, BatchDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public List<CourseDayBatchDTO> getDaywiseCoursesForAllBatches() {
        return batchRepository.findDaywiseCoursesForAllBatches();
    }

    @Override
    public BatchDTO calculateDayNumber(Long batchId) {
        Batches batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));

        LocalDate startDate = batch.getStartDate().toLocalDateTime().toLocalDate();
        LocalDate currentDate = LocalDate.now(); // Use current date instead of endDate

        long dayNumber = calculateWorkingDays(startDate, currentDate);

        // Update the entity with the calculated day number
        batch.setDayNumber(dayNumber);
        batchRepository.save(batch);

        // Convert entity to DTO
        BatchDTO batchDTO = new BatchDTO();
        batchDTO.setId(batch.getId());
        batchDTO.setBatchName(batch.getBatchName());
        batchDTO.setStartDate(batch.getStartDate());
        batchDTO.setEndDate(batch.getEndDate()); // Retain original end date
        batchDTO.setIsActive(batch.getIsActive());
        batchDTO.setDayNumber(dayNumber);

        return batchDTO;
    }

    private long calculateWorkingDays(LocalDate start, LocalDate end) {
        long count = 0;
        while (!start.isAfter(end)) {
            if (start.getDayOfWeek().getValue() < 6) { // Monday to Friday are working days (1-5)
                count++;
            }
            start = start.plusDays(1);
        }
        return count;
    }
}
