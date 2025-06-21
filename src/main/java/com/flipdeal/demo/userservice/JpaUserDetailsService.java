package com.flipdeal.demo.userservice;

import com.flipdeal.demo.exception.EmailVerificationException;
import com.flipdeal.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {

    @Value("${email-verification.required}")
    private final boolean emailVerificationRequired;

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        log.info("JpaUserDetailsService: loadUserByUsername called for username: {}", username);
        return userRepository.findByUsername(username).map(user -> {
            if (emailVerificationRequired && !user.isEmailVerified()) {
                log.warn("Email not verified for username: {}", username);
                throw new EmailVerificationException(
                        "Your email is not verified. Please verify your email before logging in");
            }
            log.info("User loaded for username: {}", username);
            return User.builder()
                    .username(username)
                    .password(user.getPassword())
                    .build();
        }).orElseThrow(() -> {
            log.error("User with username [{}] not found", username);
            return new UsernameNotFoundException("User with username [%s] not found".formatted(username));
        });
    }

}