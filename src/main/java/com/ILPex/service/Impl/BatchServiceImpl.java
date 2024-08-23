package com.ILPex.service.Impl;

import com.ILPex.DTO.BatchCreationDTO;
import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.DTO.TraineeDTO;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Trainees;
import com.ILPex.entity.Users;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.BatchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements BatchService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraineesRepository traineesRepository;

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
    public Batches createBatch(BatchCreationDTO batchCreationDTO) {
        Batches batch = new Batches();
        batch.setBatchName(batchCreationDTO.getBatchName());
        batch.setStartDate(batchCreationDTO.getStartDate());
        batch.setEndDate(batchCreationDTO.getEndDate());
        batch.setIsActive(batchCreationDTO.getIsActive());
        batch.setTrainees(batchCreationDTO.getTrainees());

        return batchRepository.save(batch);
    }

    public Batches getBatchById(Long batchId) {
        return batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID " + batchId));
    }
}
