package com.flipdeal.demo.userservice;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.flipdeal.demo.entity.User;
import com.flipdeal.demo.exception.ValidationException;
import com.flipdeal.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        log.info("UserRegistrationService: registerUser called for email: {}, username: {}", user.getEmail(),
                user.getUsername());
        final var errors = new HashMap<String, List<String>>();

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Email already taken: {}", user.getEmail());
            errors.put("email", List.of("Email [%s] is already taken".formatted(user.getEmail())));
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Username already taken: {}", user.getUsername());
            errors.put("username", List.of("Username [%s] is already taken".formatted(user.getUsername())));
        }

        if (!errors.isEmpty()) {
            log.warn("Validation errors during registration: {}", errors);
            throw new ValidationException(CONFLICT, errors);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());
        return savedUser;
    }

}