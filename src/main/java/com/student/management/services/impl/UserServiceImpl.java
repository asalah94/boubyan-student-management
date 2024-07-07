package com.student.management.services.impl;

import com.student.management.exception.ResourceNotFoundException;
import com.student.management.models.ERole;
import com.student.management.models.Role;
import com.student.management.models.User;
import com.student.management.payload.request.SignupRequest;
import com.student.management.repository.RoleRepository;
import com.student.management.repository.UserRepository;
import com.student.management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public void registerUser(SignupRequest signUpRequest) {
        User user = createUserFromSignupRequest(signUpRequest);
        Set<Role> roles = getUserRolesFromSignupRequest(signUpRequest);
        user.setRoles(roles);
        userRepository.save(user);
    }

    private User createUserFromSignupRequest(SignupRequest signUpRequest) {
        return new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()));
    }

    private Set<Role> getUserRolesFromSignupRequest(SignupRequest signUpRequest) {
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            addDefaultUserRole(roles);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        addAdminRole(roles);
                        break;
                    default:
                        addDefaultUserRole(roles);
                }
            });
        }

        return roles;
    }

    private void addDefaultUserRole(Set<Role> roles) {
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role 'ROLE_USER' not found."));
        roles.add(userRole);
    }

    private void addAdminRole(Set<Role> roles) {
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role 'ROLE_ADMIN' not found."));
        roles.add(adminRole);
    }
}
