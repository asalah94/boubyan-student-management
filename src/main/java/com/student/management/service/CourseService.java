package com.student.management.service;

import com.student.management.models.Course;
import com.student.management.payload.response.CourseDto;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    List<CourseDto> getAllCourses();
    void registerCourse(Long userId, Long courseId);
    void cancelCourseRegistration(Long userId, Long courseId);

    byte[] generateCourseSchedulePdf(List<CourseDto> courses) throws IOException;
}
