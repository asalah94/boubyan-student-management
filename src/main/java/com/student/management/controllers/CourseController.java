package com.student.management.controllers;

import com.student.management.payload.request.CourseRegistrationRequest;
import com.student.management.payload.response.CourseDto;
import com.student.management.payload.response.MessageResponse;
import com.student.management.security.services.CourseService;
import com.student.management.security.services.PdfGeneratorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        List<CourseDto> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> registerCourse(@Valid @RequestBody CourseRegistrationRequest request) {
        courseService.registerCourse(request.getUserId(), request.getCourseId());
        return ResponseEntity.ok(new MessageResponse("Course registered successfully!"));
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelCourseRegistration(@Valid @RequestBody CourseRegistrationRequest request) {
        courseService.cancelCourseRegistration(request.getUserId(), request.getCourseId());
        return ResponseEntity.ok(new MessageResponse("Course registration cancelled successfully!"));
    }

    @Autowired
    PdfGeneratorService pdfGeneratorService;

    @GetMapping("/schedule/pdf")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> getCourseSchedulePdf() {
        List<CourseDto> courses = courseService.getAllCourses();

        try {
            byte[] pdfBytes = pdfGeneratorService.generateCourseSchedulePdf(courses);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "course_schedule.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }


}
