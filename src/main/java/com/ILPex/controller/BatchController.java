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
            @RequestParam("batchData") String batchData, // JSON string of BatchCreationDTO
            @RequestParam("file") MultipartFile file) {

        ObjectMapper objectMapper = new ObjectMapper();
        BatchCreationDTO batchCreationDTO;

        try {
            batchCreationDTO = objectMapper.readValue(batchData, BatchCreationDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Parse the Excel file to extract users and trainees
        List<Users> usersList;
        try {
            usersList = parseExcelFile(file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // Save users and trainees
        for (Users user : usersList) {
            userService.saveUser(user); // Save each user, which will cascade to save the trainee
        }

        // Set the trainees to the batch and create the batch
        Set<Trainees> traineesSet = usersList.stream()
                .map(user -> user.getTrainees().iterator().next())
                .collect(Collectors.toSet());

        batchCreationDTO.setTrainees(traineesSet);

        Batches batch = batchService.createBatch(batchCreationDTO);
        return ResponseEntity.ok(batch);
    }


    @Autowired
    private RolesService rolesService;

    private List<Users> parseExcelFile(MultipartFile file) throws IOException {
        List<Users> usersList = new ArrayList<>();
        Roles traineeRole = rolesService.getRoleByName("Trainee");

        if (traineeRole == null) {
            traineeRole = rolesService.createRole("Trainee");
        }

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Iterate over rows, starting from the second row (skip header)
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                Users user = new Users();
                user.setUserName(getCellValueAsString(row.getCell(0))); // Name
                user.setEmail(getCellValueAsString(row.getCell(2))); // Email
                user.setPassword(getCellValueAsString(row.getCell(4))); // Password
                user.setRoles(traineeRole); // Set the role

                // Set up a corresponding Trainees object
                Trainees trainee = new Trainees();
                trainee.setPercipioEmail(getCellValueAsString(row.getCell(3))); // Percipio_Email
                trainee.setIsActive(true); // Assuming all new trainees are active
                trainee.setUsers(user);
                trainee.setUserUuid(UUID.randomUUID()); // Generate a unique identifier for each trainee

                // Link trainee to user
                Set<Trainees> traineesSet = new HashSet<>();
                traineesSet.add(trainee);
                user.setTrainees(traineesSet);

                usersList.add(user);
            }
        }

        return usersList;
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
