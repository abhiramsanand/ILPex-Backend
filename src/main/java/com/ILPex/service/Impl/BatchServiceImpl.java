package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.*;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.BatchService;
import com.ILPex.service.ProgramService;
import com.ILPex.service.RolesService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Autowired
    private RolesService rolesService;

    @Autowired
    private ProgramService programService;

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
        // Validate required dates
        if (batchCreationDTO.getStartDate() == null || batchCreationDTO.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }

        // Fetch the Program entity based on the program name
        Programs program = programService.findByProgramName(batchCreationDTO.getProgramName());
        if (program == null) {
            throw new IllegalArgumentException("Program not found: " + batchCreationDTO.getProgramName());
        }

        // Create a new Batch entity
        Batches batch = new Batches();
        batch.setBatchName(batchCreationDTO.getBatchName());
        batch.setStartDate(batchCreationDTO.getStartDate());
        batch.setEndDate(batchCreationDTO.getEndDate());
        batch.setIsActive(batchCreationDTO.getIsActive());
        batch.setTrainees(new HashSet<>()); // Initialize an empty HashSet for trainees
        batch.setPrograms(program); // Set the program entity

        // Save the batch to the repository

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
                        trainee.getId(),
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

    @Override
    public void deleteTrainee(Long traineeId) {
        Optional<Trainees> traineeOptional = traineesRepository.findById(traineeId);

        if (traineeOptional.isPresent()) {
            Trainees trainee = traineeOptional.get();
            Users user = trainee.getUsers();

            // Remove the trainee from the associated user
            if (user != null) {
                user.getTrainees().remove(trainee);
                userRepository.save(user); // Save user after removal of trainee
            }

            // Delete the trainee
            traineesRepository.delete(trainee);

            // Optionally delete the user if it's no longer associated with any trainee
            if (user != null && user.getTrainees().isEmpty()) {
                userRepository.delete(user);
            }
        } else {
            throw new ResourceNotFoundException("Trainee not found with ID " + traineeId);
        }
    }

    @Override
    public BatchDetailsDTO getBatchDetails(Long batchId) {
        Batches batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID " + batchId));

        List<TraineeDisplayByBatchDTO> traineeDTOs = batch.getTrainees().stream()
                .map(trainee -> new TraineeDisplayByBatchDTO(
                        trainee.getId(),
                        trainee.getUsers().getUserName(),    // Get userName from Users entity
                        trainee.getUsers().getEmail(),       // Get email from Users entity
                        trainee.getPercipioEmail(),          // Get percipioEmail from Trainees entity
                        trainee.getUsers().getPassword()     // Get password from Users entity
                ))
                .collect(Collectors.toList());

        return new BatchDetailsDTO(
                batch.getId(),
                batch.getBatchName(),
                batch.getPrograms().getProgramName(), // Assuming the program name is in the Programs entity
                batch.getIsActive(),
                batch.getStartDate().toString(),
                batch.getEndDate().toString(),
                batch.getTrainees().size(),           // Number of trainees in the batch
                traineeDTOs                           // List of TraineeDisplayByBatchDTO
        );
    }

    // In BatchServiceImpl.java

    @Override
    public TraineeDisplayByBatchDTO updateTrainee(Long traineeId, TraineeUpdateDTO traineeUpdateDTO) {
        Trainees trainee = traineesRepository.findById(traineeId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with ID " + traineeId));

        Users user = trainee.getUsers();

        // Update user details
        user.setUserName(traineeUpdateDTO.getUserName());
        user.setEmail(traineeUpdateDTO.getEmail());
        user.setPassword(traineeUpdateDTO.getPassword());

        // Update trainee details
        trainee.setPercipioEmail(traineeUpdateDTO.getPercipioEmail());
//        trainee.setIsActive(traineeUpdateDTO.getIsActive());

        // Save updated entities
        userRepository.save(user);
        traineesRepository.save(trainee);

        return modelMapper.map(trainee, TraineeDisplayByBatchDTO.class);
    }

    public void updateAllTrainees(Long batchId, List<TraineeUpdateDTO> traineeDtos) {
        // Fetch the batch to ensure it exists
        Batches batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with ID: " + batchId));

        for (TraineeUpdateDTO traineeDto : traineeDtos) {
            // Fetch the trainee by ID
            Trainees trainee = traineesRepository.findById((long) traineeDto.getTraineeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with ID: " + traineeDto.getTraineeId()));

            // Fetch the associated user
            Users user = trainee.getUsers();

            // Update the user details
            user.setUserName(traineeDto.getUserName());
            user.setEmail(traineeDto.getEmail());
            user.setPassword(traineeDto.getPassword());

            // Update trainee-specific details if needed
            trainee.setPercipioEmail(traineeDto.getPercipioEmail());
            // Update other fields as necessary

            // Save the updated user and trainee
            traineesRepository.save(trainee);
        }


    }
    public Batches updateBatch(Long batchId, BatchUpdateDTO batchUpdateDTO) {
        Batches batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));

        // Update fields only if they are not null
        if (batchUpdateDTO.getBatchName() != null) {
            batch.setBatchName(batchUpdateDTO.getBatchName());
        }
        if (batchUpdateDTO.getStartDate() != null) {
            batch.setStartDate(batchUpdateDTO.getStartDate());
        }
        if (batchUpdateDTO.getEndDate() != null) {
            batch.setEndDate(batchUpdateDTO.getEndDate());
        }

        return batchRepository.save(batch);
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

//    @PostConstruct
    public void updateDayNumbers() {
        List<Batches> batches = (List<Batches>) batchRepository.findAll();

        for (Batches batch : batches) {
            if (batch.getStartDate() != null) {
                LocalDate start = batch.getStartDate().toLocalDateTime().toLocalDate();
                LocalDate today = LocalDate.now();
                batch.setDayNumber(calculateWorkingDays(start, today));
                batchRepository.save(batch); // Save the updated batch
            }
        }
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
