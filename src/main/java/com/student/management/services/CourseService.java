package com.student.management.services;

import com.student.management.payload.response.CourseDto;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    List<CourseDto> getAllCourses();
    void registerCourse(Long userId, Long courseId);
    void cancelCourseRegistration(Long userId, Long courseId);
}
