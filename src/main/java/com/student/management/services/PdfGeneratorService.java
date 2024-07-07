package com.student.management.services;

import com.student.management.payload.response.CourseDto;

import java.io.IOException;
import java.util.List;

public interface PdfGeneratorService {
    byte[] generateCourseSchedulePdf(List<CourseDto> courses) throws IOException;
}
