package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.*;
import com.ILPex.exceptions.ResourceNotFoundException;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.repository.UserRepository;
import com.ILPex.service.BatchService;
import com.ILPex.service.ProgramService;
import com.ILPex.service.RolesService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    private CoursesRepository coursesRepository;

    @Override
    public List<BatchDTO> getBatches() {
        List<Batches> batchList = batchRepository.findAll();
        return batchList.stream().map(batch -> {
            BatchDTO batchDTO = modelMapper.map(batch, BatchDTO.class);
            batchDTO.setTotalTrainees(batch.getTrainees().size()); // Set the total number of trainees
            return batchDTO;
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

        // Find the current active batch and deactivate its trainees
        Optional<Batches> activeBatchOptional = batchRepository.findByIsActiveTrue();
        if (activeBatchOptional.isPresent()) {
            Batches activeBatch = activeBatchOptional.get();
            activeBatch.setIsActive(false);

            // Set all trainees in the previous active batch as inactive
            activeBatch.getTrainees().forEach(trainee -> {
                trainee.setIsActive(false);
                traineesRepository.save(trainee);
            });

            batchRepository.save(activeBatch); // Save the previous batch with isActive = false
        }

        // Create the new batch
        Batches newBatch = new Batches();
        newBatch.setBatchName(batchCreationDTO.getBatchName());
        newBatch.setStartDate(batchCreationDTO.getStartDate());
        newBatch.setEndDate(batchCreationDTO.getEndDate());
        newBatch.setIsActive(true); // Set the new batch as active
        newBatch.setTrainees(new HashSet<>()); // Initialize an empty HashSet for trainees
        newBatch.setPrograms(program); // Set the program entity

        // Save the new batch to the repository
        return batchRepository.save(newBatch);
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
        trainee.setIsActive(true); // Ensure trainees in the new batch are active
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
    public Batches createBatchWithTrainees(BatchCreationDTO batchCreationDTO, MultipartFile file) throws IOException {
        // Create the batch first and save it to get the generated batch ID
        Batches batch = createBatch(batchCreationDTO);

        // Parse the Excel file and set the batch ID for each trainee
        List<Users> usersList = parseExcelFile(file, batch, batchCreationDTO);

        // Save each user and trainee with the associated batch ID
        for (Users user : usersList) {
            userRepository.save(user);
        }
        Set<Trainees> traineesSet = usersList.stream()
                .map(user -> user.getTrainees().iterator().next())
                .collect(Collectors.toSet());

        batchCreationDTO.setTrainees(traineesSet);

        return batch;
    }

    private List<Users> parseExcelFile(MultipartFile file, Batches batch, BatchCreationDTO batchCreationDTO) throws IOException {
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

                String email = getCellValueAsString(row.getCell(1)); // Email
                String percipioEmail = getCellValueAsString(row.getCell(2)); // Percipio_Email

                // Validate email format
                if (!email.endsWith("@experionglobal.com")) {
                    throw new IllegalArgumentException("Invalid email format in row " + i   + ": " + email);
                }

                // Validate Percipio email format
                if (!percipioEmail.endsWith("@experionglobal.com")) {
                    throw new IllegalArgumentException("Invalid Percipio email format in row " + i  + ": " + percipioEmail);
                }

                Users user = new Users();
                String username = getCellValueAsString(row.getCell(0)).trim(); // Trim leading and trailing spaces


                if (!username.matches("^[a-zA-Z]+(\\s[a-zA-Z]+)*$")) {
                    throw new IllegalArgumentException("Invalid username format in row " + i  + ": " + username);
                }
                user.setUserName(username);
                user.setEmail(email);
                user.setPassword(getCellValueAsString(row.getCell(3))); // Password
                user.setRoles(traineeRole); // Set the role

                Trainees trainee = new Trainees();
                trainee.setPercipioEmail(percipioEmail);
                trainee.setIsActive(batchCreationDTO.getIsActive()); // Use the passed batchCreationDTO
                trainee.setUsers(user);
                trainee.setBatches(batch); // Set the batch for the trainee

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

    @PostConstruct
    public void updateDayNumbers() {
        List<Batches> batches = (List<Batches>) batchRepository.findAll();

        for (Batches batch : batches) {
            if (batch.getStartDate() != null) {
                LocalDate today = LocalDate.now();
                LocalDate courseDate = null;
                Long dayNumber = null;

                // Fetch the Courses associated with the current batch
                List<Courses> courses = coursesRepository.findByBatch_Id(batch.getId());

                // Start with today's date and keep going back until a course is found
                while (courseDate == null && !today.isBefore(batch.getStartDate().toLocalDateTime().toLocalDate())) {
                    for (Courses course : courses) {
                        LocalDate tempCourseDate = course.getCourseDate().toLocalDateTime().toLocalDate();

                        if (tempCourseDate.equals(today)) {
                            // Match found, update the courseDate and dayNumber
                            courseDate = tempCourseDate;
                            dayNumber = (long) course.getDayNumber();
                            break;
                        }
                    }

                    if (courseDate == null) {
                        // No match found for today, decrement the date
                        today = today.minusDays(1);
                    }
                }

                if (dayNumber != null) {
                    // Update the batch with the found dayNumber
                    batch.setDayNumber(dayNumber);
                    batchRepository.save(batch); // Save the updated batch
                } else {
                    System.out.println("No matching course found for batch: " + batch.getBatchName());
                }
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
