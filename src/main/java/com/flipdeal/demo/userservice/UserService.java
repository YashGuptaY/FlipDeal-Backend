package com.flipdeal.demo.userservice;

import static org.springframework.http.HttpStatus.GONE;

import java.util.UUID;

import com.flipdeal.demo.entity.User;
import com.flipdeal.demo.repository.UserRepository;
import com.flipdeal.demo.service.AuthenticationFacadeImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationFacadeImpl autthenticationFacade;

    public User getUserByUsername(final String username) {
        log.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User not found or deleted: {}", username);
            return new ResponseStatusException(GONE, "The user account has been deleted or inactivated");
        });
    }

    public User getUserId(final UUID id) {
        log.info("Fetching user by id: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found or deleted: {}", id);
            return new ResponseStatusException(GONE, "The user account has been deleted or inactivated");
        });
    }

    public UUID findByUserId() {
        String loggedInUserName = autthenticationFacade.getAuthentication().getName();
        log.info("Finding userId for logged in user: {}", loggedInUserName);
        User loggedInUser = userRepository.findByUsername(loggedInUserName).orElseThrow(() -> {
            log.error("User not found: {}", loggedInUserName);
            return new UsernameNotFoundException("User not found");
        });
        return loggedInUser.getId();
    }
}