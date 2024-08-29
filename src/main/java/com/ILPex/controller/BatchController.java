package com.ILPex.controller;

import com.ILPex.DTO.*;
import com.ILPex.constants.Message;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Roles;
import com.ILPex.entity.Trainees;
import com.ILPex.entity.Users;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.BatchService;
import com.ILPex.service.RolesService;
import com.ILPex.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/batches")
@CrossOrigin(origins = "http://localhost:5173")
public class BatchController {
    @Autowired
    private BatchService batchService;

    @Autowired
    private UserService userService;

    @Autowired
    private TraineesRepository traineesRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<BatchDTO>> getBatches() {
        List<BatchDTO> batchList = batchService.getBatches();
        return ResponseEntity.ok(batchList);
    }

    @GetMapping("/daywise-courses")
    public ResponseEntity<Object> getDaywiseCoursesForAllBatches() {
        List<CourseDayBatchDTO> courseDayBatchList = batchService.getDaywiseCoursesForAllBatches();
        return ResponseHandler.responseBuilder(Message.REQUESTED_DAYWISE_COURSE_DETAILS, HttpStatus.OK, courseDayBatchList);
    }

    @GetMapping("/{batchId}/dayNumber")
    public BatchDTO getBatchDayNumber(@PathVariable Long batchId) {
        return batchService.calculateDayNumber(batchId);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createBatch(
            @RequestParam("batchData") String batchData,
            @RequestParam("file") MultipartFile file) {

        ObjectMapper objectMapper = new ObjectMapper();
        BatchCreationDTO batchCreationDTO;

        try {
            batchCreationDTO = objectMapper.readValue(batchData, BatchCreationDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            Batches batch = batchService.createBatchWithTrainees(batchCreationDTO, file);
            return ResponseEntity.ok(batch.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{batchId}/trainees")
    public ResponseEntity<List<TraineeDisplayByBatchDTO>> getTraineesByBatchId(@PathVariable("batchId") Long batchId) {
        List<TraineeDisplayByBatchDTO> trainees = batchService.getTraineesByBatchId(batchId);
        return ResponseEntity.ok(trainees);
    }

    @PostMapping("/{batchId}/trainees")
    public ResponseEntity<TraineeDisplayByBatchDTO> addTraineeToBatch(
            @PathVariable Long batchId,
            @RequestBody TraineeCreationDTO traineeCreationDTO) {

        TraineeDisplayByBatchDTO createdTrainee = batchService.addTraineeToBatch(batchId, traineeCreationDTO);
        return new ResponseEntity<>(createdTrainee, HttpStatus.CREATED);
    }

    @GetMapping("/{batchId}")
    public ResponseEntity<Batches> getBatchById(@PathVariable Long batchId) {
        Batches batch = batchService.getBatchById(batchId);
        if (batch != null) {
            return ResponseEntity.ok(batch);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/trainees/{traineeId}")
    public ResponseEntity<String> deleteTrainee(@PathVariable("traineeId") Long traineeId) {
        try {
            // Find the Trainee entity
            Optional<Trainees> traineeOptional = traineesRepository.findById(traineeId);

            if (traineeOptional.isPresent()) {
                Trainees trainee = traineeOptional.get();

                // Find the associated user
                Users user = trainee.getUsers();

                // Delete the trainee
                traineesRepository.delete(trainee);
                // Delete the associated user
                if (user != null) {
                    userRepository.delete(user);
                }

                return ResponseEntity.ok("Trainee and associated user deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found.");
            }
        } catch (Exception e) {
            // Log the exception (e.g., using SLF4J)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting trainee.");
        }
    }


    @GetMapping("/{batchId}/details")
    public ResponseEntity<BatchDetailsDTO> getBatchDetails(@PathVariable("batchId") Long batchId) {
        try {
            BatchDetailsDTO batchDetails = batchService.getBatchDetails(batchId);
            return ResponseEntity.ok(batchDetails);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PutMapping("/trainees/{traineeId}")
    public ResponseEntity<TraineeDisplayByBatchDTO> updateTrainee(
            @PathVariable("traineeId") Long traineeId,
            @RequestBody TraineeUpdateDTO traineeUpdateDTO) {
        try {
            TraineeDisplayByBatchDTO updatedTrainee = batchService.updateTrainee(traineeId, traineeUpdateDTO);
            return ResponseEntity.ok(updatedTrainee);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PutMapping("/{batchId}/trainees")
    public ResponseEntity<Map<String, String>> updateAllTrainees(
            @PathVariable Long batchId,
            @RequestBody List<TraineeUpdateDTO> traineeDtos) {
        try {
            batchService.updateAllTrainees(batchId, traineeDtos);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Trainees updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error updating trainees");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
