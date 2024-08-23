package com.ILPex.controller;

import com.ILPex.DTO.BatchCreationDTO;
import com.ILPex.DTO.BatchDTO;
import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.DTO.TraineeDTO;
import com.ILPex.constants.Message;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Roles;
import com.ILPex.entity.Trainees;
import com.ILPex.entity.Users;
import com.ILPex.repository.BatchRepository;
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
    public ResponseEntity<Batches> createBatch(
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

        // Create the batch first and save it to get the generated batch ID
        Batches batch = batchService.createBatch(batchCreationDTO);
        logger.info("Created batch with ID: {}", batch.getId());

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

        Batches updatedBatch = batchService.getBatchById(batch.getId());

        return ResponseEntity.ok(updatedBatch);


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
                trainee.setPercipioEmail(getCellValueAsString(row.getCell(3))); // Percipio_Email
                trainee.setIsActive(true);
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

}
