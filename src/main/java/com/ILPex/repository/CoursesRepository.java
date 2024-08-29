package com.ILPex.repository;

import com.ILPex.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface CoursesRepository extends JpaRepository<Courses,Long> {
    List<Courses> findAllByCourseDateGreaterThanEqual(Timestamp courseDate);
    @Query("SELECT c FROM Courses c ORDER BY c.dayNumber ASC, c.courseDate ASC")
    List<Courses> findAllOrderByDayNumberAndCourseDate();
    List<Courses> findAllByOrderByDayNumberAscCourseDateAsc();
    List<Courses> findByCourseDate(Timestamp courseDate);
}
