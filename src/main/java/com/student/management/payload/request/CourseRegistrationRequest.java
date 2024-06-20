package com.student.management.payload.request;

import jakarta.validation.constraints.NotNull;

public class CourseRegistrationRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long courseId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
