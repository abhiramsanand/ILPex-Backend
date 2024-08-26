package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Roles;
import com.ILPex.entity.Trainees;
import com.ILPex.entity.Users;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.BatchService;
import com.ILPex.service.RolesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl implements BatchService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public Batches createBatch(BatchCreationDTO batchCreationDTO) {
        if (batchCreationDTO.getStartDate() == null || batchCreationDTO.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }

        Batches batch = new Batches();
        batch.setBatchName(batchCreationDTO.getBatchName());
        batch.setStartDate(batchCreationDTO.getStartDate());
        batch.setEndDate(batchCreationDTO.getEndDate());
        batch.setIsActive(batchCreationDTO.getIsActive());
        batch.setTrainees(new HashSet<>()); // Initialize an empty HashSet for trainees

        return batchRepository.save(batch);
    }

    @Override
    public Batches getBatchById(Long batchId) {
        return batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID " + batchId));
    }

    @Override
    public List<TraineeDisplayByBatchDTO> getTraineesByBatchId(Long batchId) {
        Batches batch = getBatchById(batchId); // This method will throw ResourceNotFoundException if batch not found
        return batch.getTrainees().stream()
                .map(trainee -> new TraineeDisplayByBatchDTO(
                        trainee.getUsers().getUserName(),
                        trainee.getUsers().getEmail(),
                        trainee.getPercipioEmail(),
                        trainee.getUsers().getPassword()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public TraineeDisplayByBatchDTO addTraineeToBatch(Long batchId, TraineeCreationDTO traineeCreationDTO) {
        Batches batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id " + batchId));

        Roles traineeRole = rolesService.getRoleByName("Trainee");
        if (traineeRole == null) {
            traineeRole = rolesService.createRole("Trainee");
        }

// Map TraineeCreationDTO to Users entity
        Users user = modelMapper.map(traineeCreationDTO, Users.class);
        user.setRoles(traineeRole);

// Map the TraineeDTO to the Trainees entity
        Trainees trainee = new Trainees();
        trainee.setPercipioEmail(traineeCreationDTO.getPercipioEmail());
        trainee.setIsActive(batch.getIsActive());
        trainee.setUsers(user);
        trainee.setBatches(batch);
        user.setTrainees(new HashSet<>(List.of(trainee)));

// Save the user and trainee
        userRepository.save(user);

        return modelMapper.map(trainee, TraineeDisplayByBatchDTO.class);
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
