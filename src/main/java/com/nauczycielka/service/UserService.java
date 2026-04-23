package com.nauczycielka.service;

import com.nauczycielka.model.User;
import com.nauczycielka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    private final com.nauczycielka.repository.UserRepository userRepository;

    public UserService(com.nauczycielka.repository.UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Transactional
    public User createUser(String email, String fullName, String password, com.nauczycielka.model.UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists");
        }

        User user = User.builder()
                .email(email)
                .fullName(fullName)
                .password(password)
                .role(role)
                .build();

        return userRepository.save(user);
    }
}
