package com.student.management.payload.response;

import com.student.management.models.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

public class CourseDto {

    private String title;

    private String description;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
