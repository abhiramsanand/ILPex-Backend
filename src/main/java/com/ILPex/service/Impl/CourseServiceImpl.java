package com.ILPex.service.Impl;

import com.ILPex.DTO.*;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Courses;
import com.ILPex.entity.DailyReports;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.DailyReportsRepository;
import com.ILPex.entity.Holiday;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.repository.HolidayRepository;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {


    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private BatchRepository batchesRepository;

    @Autowired
    private DailyReportsRepository dailyReportsRepository;

    @Autowired
    private HolidayRepository holidayRepository;


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
    public void saveCourses(List<Courses> courses) {
        coursesRepository.saveAll(courses);
        checkAndMarkEmptyDaysAsHolidays();
    }

    @Override
    public List<LocalDate> getAllCourseDates() {
        return coursesRepository.findAll().stream()
                .map(course -> course.getCourseDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .collect(Collectors.toList());
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

    @Override
    public List<CourseDailyReportDTO> getCourseDetails(Long traineeId, Long batchId) {
        List<CourseDailyReportDTO> responseList = new ArrayList<>();
        Date currentDate = new Date(); // Current date

        // Fetch courses for the given batchId where course_date is before current date
        List<Courses> courses = coursesRepository.findByBatchIdAndCourseDateBefore(batchId, currentDate);

        // Iterate over each course to find corresponding daily report
        for (Courses course : courses) {
            CourseDailyReportDTO dto = new CourseDailyReportDTO();
            dto.setCourseDate(course.getCourseDate());
            dto.setDayNumber(course.getDayNumber());
            dto.setCourseName(course.getCourseName());

            // Fetch daily report based on traineeId and courseId
            Optional<DailyReports> dailyReportOpt = dailyReportsRepository.findByTrainees_IdAndCourses_Id(traineeId, course.getId());
            if (dailyReportOpt.isPresent()) {
                DailyReports dailyReport = dailyReportOpt.get();
                dto.setTimeTaken(dailyReport.getTimeTaken());
                dto.setDailyReportId(dailyReport.getId());
            } else {
                dto.setTimeTaken(0); // No entry means time taken is 0
                dto.setDailyReportId(null); // No daily report found
            }
            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public List<PendingSubmissionDTO> getPendingSubmissions(Long batchId, Long traineeId) {
        List<Courses> pendingCourses = coursesRepository.findPendingSubmissions(batchId, traineeId);
        return pendingCourses.stream().map(course -> new PendingSubmissionDTO(course.getId(), course.getCourseName(), course.getDayNumber()))
                .collect(Collectors.toList());
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

    @Override
    public List<DayNumberWithDateDTO> getAllCourseDatesWithDayNumber() {
        return new ArrayList<>(coursesRepository.findAll().stream()
                .collect(Collectors.toMap(
                        course -> course.getCourseDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        course -> new DayNumberWithDateDTO(
                                course.getCourseDate().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate(),
                                course.getDayNumber()),
                        (existing, replacement) -> replacement // If a duplicate key is found, keep the last one
                ))
                .values());
    }

    private final List<LocalDate> holidays = List.of();

    @Override
    public void updateCourseDatesForHoliday(LocalDate holidayDate, String description) {
        Holiday holiday = new Holiday();
        holiday.setDate(holidayDate);
        holiday.setDescription(description);
        holidayRepository.save(holiday);

        LocalDate startDate = holidayDate.minusDays(6);
        LocalDate endDate = holidayDate.plusDays(6);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (isWeekend(date)) {
                Holiday weekendHoliday = new Holiday();
                weekendHoliday.setDate(date);
                weekendHoliday.setDescription("Weekend");
                holidayRepository.save(weekendHoliday);
            }
        }

        Timestamp holidayTimestamp = convertToTimestamp(holidayDate);

        List<Courses> courses = coursesRepository.findAllByOrderByDayNumberAscCourseDateAsc();
        Map<Integer, Timestamp> updatedDates = new HashMap<>();

        LocalDate nextWorkingDay = findNextWorkingDay(holidayDate.plusDays(1));
        Timestamp nextWorkingDayTimestamp = convertToTimestamp(nextWorkingDay);

        for (Courses course : courses) {
            int dayNumber = course.getDayNumber();
            Timestamp currentDate = course.getCourseDate();

            if (currentDate.toLocalDateTime().toLocalDate().isAfter(holidayDate) ||
                    currentDate.toLocalDateTime().toLocalDate().isEqual(holidayDate)) {
                if (!updatedDates.containsKey(dayNumber)) {
                    updatedDates.put(dayNumber, nextWorkingDayTimestamp);
                    nextWorkingDay = findNextWorkingDay(nextWorkingDay.plusDays(1));
                    nextWorkingDayTimestamp = convertToTimestamp(nextWorkingDay);
                }
                course.setCourseDate(updatedDates.get(dayNumber));
            }
        }

        coursesRepository.saveAll(courses);
        checkAndMarkEmptyDaysAsHolidays();
    }


    private LocalDate findNextWorkingDay(LocalDate date) {
        while (isHoliday(date) || isWeekend(date)) {
            date = date.plusDays(1);
        }
        return date;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private boolean isHoliday(LocalDate date) {
        return holidays.contains(date) || holidayRepository.existsById(date);
    }

    private Timestamp convertToTimestamp(LocalDate date) {
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.systemDefault());
        return Timestamp.from(zonedDateTime.toInstant());
    }

    @Override
    public void restoreCourseDatesForWorkingDay(LocalDate holidayDate) {
        // Fetch all courses sorted by day number and course date
        List<Courses> courses = coursesRepository.findAllByOrderByDayNumberAscCourseDateAsc();

        // Maps to keep track of changes
        Map<Integer, LocalDate> dayNumberToOriginalDate = new HashMap<>();
        List<Courses> updatedCourses = new ArrayList<>();

        // Determine the original dates before the holiday was applied
        LocalDate nextWorkingDay = findNextWorkingDay(holidayDate.plusDays(1));
        LocalDate shiftedDate = nextWorkingDay.minusDays(1); // This should be the unmarked holiday date

        boolean shouldShift = false; // Flag to start shifting dates after the unmarked holiday

        for (Courses course : courses) {
            int dayNumber = course.getDayNumber();
            LocalDate courseDate = course.getCourseDate().toLocalDateTime().toLocalDate();

            if (courseDate.equals(nextWorkingDay)) {
                // Restore the course that was scheduled on the unmarked holiday
                course.setCourseDate(Timestamp.valueOf(shiftedDate.atStartOfDay()));
                updatedCourses.add(course);
                shouldShift = true; // Start shifting subsequent courses
                System.out.println("Restored course with Day Number: " + dayNumber + " to Date: " + shiftedDate);
            } else if (shouldShift && courseDate.isAfter(holidayDate)) {
                // For courses after the holiday, use the previous working day logic
                LocalDate originalDate = findPreviousWorkingDay(courseDate.minusDays(1)); // Shift back one day
                course.setCourseDate(Timestamp.valueOf(originalDate.atStartOfDay()));
                updatedCourses.add(course);
                System.out.println("Updated course with Day Number: " + dayNumber + " to Date: " + originalDate);
            } else {
                // For courses before the holiday, ensure dates are correct (no changes needed here)
                updatedCourses.add(course);
                System.out.println("No change for course with Day Number: " + dayNumber);
            }
        }

        // Save all updated courses back to the repository
        coursesRepository.saveAll(updatedCourses);

        // Remove the holiday from the holiday table
        holidayRepository.deleteById(holidayDate);
        System.out.println("Holiday on " + holidayDate + " has been removed.");
    }


    private LocalDate findPreviousWorkingDay(LocalDate date) {
        while (isHoliday(date) || isWeekend(date)) {
            date = date.minusDays(1);
        }
        return date;
    }

    private void checkAndMarkEmptyDaysAsHolidays() {
        // Fetch all course dates
        List<LocalDate> courseDates = getAllCourseDates();

        // Determine the earliest date to start from
        LocalDate startDate = courseDates.isEmpty() ? LocalDate.now() : courseDates.get(0).withDayOfMonth(1);

        // Determine the end date as the last course date
        LocalDate endDate = courseDates.isEmpty() ? LocalDate.now() : Collections.max(courseDates);

        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            // Log the date being processed
            System.out.println("Processing date: " + date);

            if (!isWeekend(date) && !courseDates.contains(date) && !isHoliday(date)) {
                System.out.println("Marking as holiday: " + date);

                Holiday holiday = new Holiday();
                holiday.setDate(date);
                holiday.setDescription("Automatically marked as holiday (no courses)");
                holidayRepository.save(holiday);
            } else {
                // Log if the date is skipped
                if (isWeekend(date)) {
                    System.out.println("Skipping weekend: " + date);
                } else if (courseDates.contains(date)) {
                    System.out.println("Skipping course date: " + date);
                } else if (isHoliday(date)) {
                    System.out.println("Skipping existing holiday: " + date);
                }
            }
        }
    }

    @Override
    public List<CourseTraineeProgressDTO> getCoursesWithProgress(Long traineeId, Long batchId, Timestamp courseDate) {
        return coursesRepository.findCoursesWithProgress(traineeId, batchId, courseDate);
    }

}