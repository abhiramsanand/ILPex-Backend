package com.ILPex.service.Impl;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

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
}
