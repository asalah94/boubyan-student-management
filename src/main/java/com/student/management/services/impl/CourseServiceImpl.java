package com.student.management.services.impl;

import com.student.management.exception.ResourceNotFoundException;
import com.student.management.models.Course;
import com.student.management.models.User;
import com.student.management.payload.response.CourseDto;
import com.student.management.repository.CourseRepository;
import com.student.management.repository.UserRepository;
import com.student.management.services.CourseService;
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
