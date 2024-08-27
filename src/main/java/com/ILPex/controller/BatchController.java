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
    public ResponseEntity<Batches> createBatch(
            @RequestParam("batchData") String batchData,
            @RequestParam("file") MultipartFile file) {

        ObjectMapper objectMapper = new ObjectMapper();
        BatchCreationDTO batchCreationDTO;

        try {
            batchCreationDTO = objectMapper.readValue(batchData, BatchCreationDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Create the batch first and save it to get the generated batch ID
        Batches batch = batchService.createBatch(batchCreationDTO);

        // Parse the Excel file and set the batch ID for each trainee
        List<Users> usersList;
        try {
            usersList = parseExcelFile(file, batch);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // Save each user and trainee with the associated batch ID
        for (Users user : usersList) {
            userService.saveUser(user);
        }
        Set<Trainees> traineesSet = usersList.stream()
                .map(user -> user.getTrainees().iterator().next())
                .collect(Collectors.toSet());

        batchCreationDTO.setTrainees(traineesSet);
        return ResponseEntity.ok(batch);
    }

    @Autowired
    private RolesService rolesService;

    private List<Users> parseExcelFile(MultipartFile file, Batches batch) throws IOException {
        List<Users> usersList = new ArrayList<>();
        Roles traineeRole = rolesService.getRoleByName("Trainee");

        if (traineeRole == null) {
            traineeRole = rolesService.createRole("Trainee");
        }

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    break;
                }

                Users user = new Users();
                user.setUserName(getCellValueAsString(row.getCell(0))); // Name
                user.setEmail(getCellValueAsString(row.getCell(2))); // Email
                user.setPassword(getCellValueAsString(row.getCell(4))); // Password
                user.setRoles(traineeRole); // Set the role

                Trainees trainee = new Trainees();
                BatchCreationDTO batchCreationDTO = new BatchCreationDTO();
                trainee.setPercipioEmail(getCellValueAsString(row.getCell(3))); // Percipio_Email
                trainee.setIsActive(batchCreationDTO.getIsActive());
                trainee.setUsers(user);
                trainee.setBatches(batch); // Set the batch for the trainee
//                trainee.setUserUuid(UUID.randomUUID());

                Set<Trainees> traineesSet = new HashSet<>();
                traineesSet.add(trainee);
                user.setTrainees(traineesSet);

                usersList.add(user);
            }
        }

        return usersList;
    }


    private boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }


    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
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
