package com.student.management.security.services.impl;

import com.student.management.exception.ResourceNotFoundException;
import com.student.management.models.Course;
import com.student.management.models.User;
import com.student.management.payload.response.CourseDto;
import com.student.management.repository.CourseRepository;
import com.student.management.repository.UserRepository;
import com.student.management.security.services.CourseService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable("courses")
    public List<CourseDto> getAllCourses() {
        return mapToDtoList(courseRepository.findAll());
    }

    @Override
    public void registerCourse(Long userId, Long courseId) {
        User user = getUserById(userId);
        Course course = getCourseById(courseId);
        user.getCourses().add(course);
        userRepository.save(user);
    }

    @Override
    public void cancelCourseRegistration(Long userId, Long courseId) {
        User user = getUserById(userId);
        Course course = getCourseById(courseId);
        user.getCourses().remove(course);
        userRepository.save(user);
    }

    @Override
    public byte[] generateCourseSchedulePdf(List<CourseDto> courses) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                writeCourseSchedule(contentStream, courses);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IOException("Failed to generate course schedule PDF", e);
        }
    }

    private void writeCourseSchedule(PDPageContentStream contentStream, List<CourseDto> courses) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Course Schedule");
        contentStream.newLine();
        contentStream.newLine();

        for (CourseDto course : courses) {
            contentStream.showText("Title: " + course.getTitle());
            contentStream.newLine();
            contentStream.showText("Description: " + course.getDescription());
            contentStream.newLine();
            contentStream.newLine();
        }

        contentStream.endText();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }

    private CourseDto mapToDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(course.getTitle());
        courseDto.setDescription(course.getDescription());
        return courseDto;
    }

    private List<CourseDto> mapToDtoList(List<Course> courses) {
        return courses.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
