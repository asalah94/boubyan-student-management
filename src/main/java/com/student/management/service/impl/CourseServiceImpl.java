package com.student.management.service.impl;

import com.student.management.models.Course;
import com.student.management.models.User;
import com.student.management.payload.response.CourseDto;
import com.student.management.repository.CourseRepository;
import com.student.management.repository.UserRepository;
import com.student.management.service.CourseService;
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

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

     @Override
     @Cacheable("courses")
    public List<CourseDto> getAllCourses() {
        return mapToDtoList(courseRepository.findAll());
    }

    @Override
    public void registerCourse(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        user.getCourses().add(course);
        userRepository.save(user);
    }

    @Override
    public void cancelCourseRegistration(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        user.getCourses().remove(course);
        userRepository.save(user);
    }

    @Override
        public byte[] generateCourseSchedulePdf(List<CourseDto> courses) throws IOException {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
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

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            document.close();
            return outputStream.toByteArray();
        }


    public CourseDto mapToDto(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(course.getTitle());
        courseDto.setDescription(course.getDescription());
        return courseDto;
    }

    public List<CourseDto> mapToDtoList(List<Course> courses) {
        return courses.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
