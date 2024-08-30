package com.ILPex.service.Impl;

import com.ILPex.DTO.CourseDayBatchDTO;
import com.ILPex.DTO.CourseDurationDTO;
import com.ILPex.DTO.TotalCourseDaysDTO;
import com.ILPex.DTO.TotalCourseDurationDTO;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Courses;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.TraineesRepository;
import com.ILPex.service.CourseService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {


    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private BatchRepository batchesRepository;


    @Override
    public List<CourseDayBatchDTO> getCoursesByBatchId(Long batchId) {
        return coursesRepository.findCoursesByBatchId(batchId);
    }


//    @Override
//    public CourseDurationDTO getTotalCourseDuration(Long batchId) {
//        Long totalDurationMinutes = coursesRepository.findTotalCourseDurationByBatchId(batchId);
//        return new CourseDurationDTO(totalDurationMinutes != null ? totalDurationMinutes : 0);
//    }

    @Override
    public TotalCourseDaysDTO getTotalCourseDaysCompleted(Long batchId) {
        Long totalDays = coursesRepository.countDistinctCourseDaysCompletedByBatchId(batchId);
        return new TotalCourseDaysDTO(totalDays);
    }

    @Override
    public TotalCourseDurationDTO getTotalCourseDuration(Long batchId) {
        // Fetch the day_number for the specified batch
        Integer dayNumber = batchesRepository.findDayNumberById(batchId);

        if (dayNumber == null) {
            throw new RuntimeException("Batch not found with ID: " + batchId);
        }

        // Fetch the total course duration up to the day number
        Long totalDuration = coursesRepository.getTotalCourseDurationUpToDayNumber(dayNumber);

        return new TotalCourseDurationDTO(totalDuration);
    }

    @Override
    public void saveCourses(List<Courses> coursesList) {
        coursesRepository.saveAll(coursesList);
    }

    @Override
    public List<Courses> parseCourseExcelFile(MultipartFile file, Batches batch) throws IOException {
        List<Courses> coursesList = new ArrayList<>();
        Integer lastDayNumber = null;
        LocalDate lastCourseDate = null;

        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Skip the header row
            int startRow = 2;

            for (int i = startRow; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                Courses course = new Courses();

                // Get the day number or use the previous value if current is null
                Integer dayNumber = getCellValueAsInteger(row.getCell(0));
                if (dayNumber != null) {
                    lastDayNumber = dayNumber;
                }
                course.setDayNumber(lastDayNumber != null ? lastDayNumber : null);

                // Get the course date or use the previous date if null
                LocalDate courseDate = getCellValueAsDate(row.getCell(1));
                if (courseDate != null) {
                    lastCourseDate = courseDate;
                }
                course.setCourseDate(lastCourseDate != null ? Timestamp.valueOf(lastCourseDate.atStartOfDay()) : null);

                // Get the course name directly from the Excel cell
                String courseName = getCellValueAsString(row.getCell(3)).trim();
                if (courseName.isEmpty() || !isValidCourseName(courseName)) {
                    continue; // Skip if course name is invalid or empty
                }
                course.setCourseName(courseName);

                // Get the course type directly from the Excel cell
                String courseType = getCellValueAsString(row.getCell(2)).trim();
                if (courseType.isEmpty() || !isValidCourseType(courseType)) {
                    continue; // Skip if course type is invalid or empty
                }
                course.setCourseType(courseType);

                // Set course duration
                course.setCourseDuration(getCellValueAsString(row.getCell(5))); // Duration

                // Set the batch for the course
                course.setBatch(batch);

                // Add course to the list
                coursesList.add(course);
            }
        }

        return coursesList;
    }

    private boolean isValidCourseName(String courseName) {
        return !courseName.trim().isEmpty();
    }

    private boolean isValidCourseType(String courseType) {
        return !courseType.trim().isEmpty();
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

    private LocalDate getCellValueAsDate(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell != null) {
            try {
                switch (cell.getCellType()) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            throw new IllegalArgumentException("Cell contains date, not integer.");
                        }
                        return (int) cell.getNumericCellValue();
                    case STRING:
                        String cellValue = cell.getStringCellValue().trim();
                        if (cellValue.isEmpty() || cellValue.equalsIgnoreCase("DayNumber")) {
                            return null;
                        }
                        return Integer.parseInt(cellValue);
                    default:
                        throw new IllegalArgumentException("Unsupported cell type: " + cell.getCellType());
                }
            } catch (NumberFormatException e) {
                System.err.println("Failed to convert cell value to integer: " + cell.getStringCellValue());
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing cell value: " + e.getMessage());
            }
        } else {
            System.err.println("Cell is null.");
        }
        return null;
    }
}