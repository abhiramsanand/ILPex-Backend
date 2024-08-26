package com.ILPex.controller;

import com.ILPex.DTO.*;
import com.ILPex.constants.Message;
import com.ILPex.entity.*;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.response.BatchIdResponse;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.BatchService;
import com.ILPex.service.ProgramService;
import com.ILPex.service.RolesService;
import com.ILPex.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

    @Autowired
    private BatchService batchService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private TraineesRepository traineesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private UserService userService;

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

    @PostMapping("/create")
    public ResponseEntity<BatchIdResponse> createBatch(
            @RequestParam("batchData") String batchData,
            @RequestParam("file") MultipartFile file) {

        logger.info("Received batch creation request with batchData: {}", batchData);

        ObjectMapper objectMapper = new ObjectMapper();
        BatchCreationDTO batchCreationDTO;

        try {
            batchCreationDTO = objectMapper.readValue(batchData, BatchCreationDTO.class);
            logger.info("Parsed batchCreationDTO: {}", batchCreationDTO);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing batch data", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Fetch the Program entity based on the program name
        Programs program = programService.findByProgramName(batchCreationDTO.getProgramName());
        if (program == null) {
            logger.error("Program not found: {}", batchCreationDTO.getProgramName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Set the program in the batch entity
        Batches batch = new Batches();
        batch.setPrograms(program);  // Set the program entity in the batch

        // Set other batch details from DTO
        batch.setBatchName(batchCreationDTO.getBatchName());
        batch.setStartDate(batchCreationDTO.getStartDate());
        batch.setEndDate(batchCreationDTO.getEndDate());
        batch.setIsActive(batchCreationDTO.getIsActive());

        // Save the batch to get the generated batch ID
        batch = batchService.createBatch(batchCreationDTO);
        Long batchId = batch.getId();  // Assuming batch ID is of type Long

        logger.info("Created batch with ID: {}", batchId);

        // Parse the Excel file and set the batch ID for each trainee
        List<Users> usersList;
        try {
            usersList = parseExcelFile(file, batch);
            logger.info("Parsed usersList from file: {}", usersList);
        } catch (IOException e) {
            logger.error("Error parsing Excel file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // Save each user and trainee with the associated batch ID
        for (Users user : usersList) {
            userService.saveUser(user);
            logger.info("Saved user: {}", user);
        }

        Set<Trainees> traineesSet = usersList.stream()
                .map(user -> user.getTrainees().iterator().next())
                .collect(Collectors.toSet());

        batchCreationDTO.setTrainees(traineesSet);

        // Return the batch ID as a response object
        BatchIdResponse response = new BatchIdResponse(batchId);
        return ResponseEntity.ok(response);
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
//    @PutMapping("/{batchId}/trainees")
//    public ResponseEntity<Void> updateTrainees(@PathVariable Long batchId, @RequestBody List<TraineeDisplayByBatchDTO> traineeDtos) {
//        try {
//            traineeService.updateTrainees(batchId, traineeDtos);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

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
            logger.error("Error deleting trainee with ID: " + traineeId, e);
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
            logger.error("Error fetching batch details for ID: " + batchId, e);
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
            logger.error("Error updating trainee with ID: " + traineeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    }








