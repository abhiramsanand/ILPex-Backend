package com.ILPex.service.Impl;

import com.ILPex.DTO.DayNumberWithDateDTO;
import com.ILPex.entity.Batches;
import com.ILPex.entity.Courses;
import com.ILPex.repository.BatchRepository;
import com.ILPex.repository.CoursesRepository;
import com.ILPex.service.CoursesService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CoursesService {
    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private BatchRepository batchesRepository;

//    @Override
//    public void saveCoursesFromExcel(MultipartFile file, Long batchId) throws IOException {
//        try (InputStream inputStream = file.getInputStream()) {
//            Workbook workbook = WorkbookFactory.create(inputStream);
//            Sheet sheet = workbook.getSheetAt(0);
//            Iterator<Row> rowIterator = sheet.iterator();
//
//            // Skip the header row
//            if (rowIterator.hasNext()) {
//                rowIterator.next();
//            }
//
//            Batches batch = batchesRepository.findById(batchId)
//                    .orElseThrow(() -> new RuntimeException("Batch not found with ID: " + batchId));
//
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy");
//
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//
//                Courses course = new Courses();
//
//                // Read and set the Day Number
//                course.setDayNumber((int) row.getCell(0).getNumericCellValue());
//
//                // Read and set the Course Date
//                String dateStr = row.getCell(1).getStringCellValue();
//                LocalDate courseDate = LocalDate.parse(dateStr, formatter);
//                course.setCourseDate(Timestamp.valueOf(courseDate.atStartOfDay()));
//
//                // Read and set the Course Type
//                course.setCourseType(row.getCell(2).getStringCellValue());
//
//                // Read and set the Course Name
//                course.setCourseName(row.getCell(3).getStringCellValue());
//
//                // Read and set the Course Duration
//                course.setCourseDuration(row.getCell(5).getStringCellValue());
//
//                // Associate the course with the batch
//                course.setBatch(batch);
//
//                // Save the course
//                coursesRepository.save(course);
//            }
//        }
//    }
    @Override
    public void saveCourses(List<Courses> courses) {
        coursesRepository.saveAll(courses);
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
    public List<DayNumberWithDateDTO> getAllCourseDatesWithDayNumber() {
        return coursesRepository.findAll().stream()
                .map(course -> new DayNumberWithDateDTO(
                        course.getCourseDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        course.getDayNumber()))
                .collect(Collectors.toList());
    }

    @Override
    public void shiftCourseDates(LocalDate holiday) {
        // Fetch all courses sorted by day number and course date
        List<Courses> courses = coursesRepository.findAllOrderByDayNumberAndCourseDate();

        LocalDate nextAvailableDate = holiday;

        for (Courses course : courses) {
            // Convert java.util.Date to LocalDate
            LocalDate courseDate = course.getCourseDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // If the course date is before the holiday, skip to the next course
            if (courseDate.isBefore(holiday)) {
                nextAvailableDate = courseDate;
                continue;
            }

            // Calculate the next available working day (skipping weekends)
            do {
                nextAvailableDate = nextAvailableDate.plusDays(1);
            } while (nextAvailableDate.getDayOfWeek().getValue() >= 6); // Skip Saturday and Sunday

            // Set the new course date for the current day number
            course.setCourseDate(Timestamp.valueOf(nextAvailableDate.atStartOfDay()));
        }

        // Save all updated courses back to the repository
        coursesRepository.saveAll(courses);
    }

    private final List<LocalDate> holidays = List.of(); // You can maintain a list of holidays if needed

    public void updateCourseDatesForHoliday(LocalDate holidayDate) {
        Timestamp holidayTimestamp = convertToTimestamp(holidayDate);

        // Retrieve all courses sorted by day number and course date
        List<Courses> courses = coursesRepository.findAllByOrderByDayNumberAscCourseDateAsc();

        // Map to store the updated dates for each day number
        Map<Integer, Timestamp> updatedDates = new HashMap<>();

        LocalDate nextWorkingDay = findNextWorkingDay(holidayDate.plusDays(1));
        Timestamp nextWorkingDayTimestamp = convertToTimestamp(nextWorkingDay);

        for (Courses course : courses) {
            int dayNumber = course.getDayNumber();
            Timestamp currentDate = course.getCourseDate();

            if (currentDate.toLocalDateTime().toLocalDate().isAfter(holidayDate) || currentDate.toLocalDateTime().toLocalDate().isEqual(holidayDate)) {
                if (!updatedDates.containsKey(dayNumber)) {
                    updatedDates.put(dayNumber, nextWorkingDayTimestamp);
                    nextWorkingDay = findNextWorkingDay(nextWorkingDay.plusDays(1));
                    nextWorkingDayTimestamp = convertToTimestamp(nextWorkingDay);
                }
                course.setCourseDate(updatedDates.get(dayNumber));
            }
        }

        // Save the updated courses
        coursesRepository.saveAll(courses);
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
        return holidays.contains(date); // Modify this logic based on how you store holidays
    }

    private Timestamp convertToTimestamp(LocalDate date) {
        // Convert LocalDate to ZonedDateTime at start of the day in the system default zone
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.systemDefault());
        // Convert ZonedDateTime to Timestamp
        return Timestamp.from(zonedDateTime.toInstant());
    }

}
