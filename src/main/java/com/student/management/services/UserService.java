package com.student.management.services;

import com.student.management.payload.request.SignupRequest;

public interface UserService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void registerUser(SignupRequest signUpRequest);
}
