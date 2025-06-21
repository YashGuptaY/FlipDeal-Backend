package com.flipdeal.demo.controller;

import com.flipdeal.demo.dto.RegistrationRequestDto;
import com.flipdeal.demo.dto.RegistrationResponseDto;
import com.flipdeal.demo.mapper.UserRegistrationMapper;
import com.flipdeal.demo.userservice.UserRegistrationService;
import com.flipdeal.demo.userservice.ProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    @Value("${email-verification.required}")
    private final boolean emailVerificationRequired;

    private final UserRegistrationService userRegistrationService;

    private final UserRegistrationMapper userRegistrationMapper;
    
    private final ProfileServiceImpl profileServiceImpl; 

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> registerUser(
            @Valid @RequestBody final RegistrationRequestDto registrationDTO) {
        log.info("POST /api/auth/register called for username: {}", registrationDTO.username());
        final var registeredUser = userRegistrationService
                .registerUser(userRegistrationMapper.toEntity(registrationDTO));
        registeredUser.getId();
        log.info("User registered: {}", registeredUser.getUsername()+" with userId: {}",registeredUser.getId());
        if (emailVerificationRequired) {
        	profileServiceImpl.sendOtp(registeredUser.getEmail());
            log.info("Verification email sent to: {}", registeredUser.getEmail());
        }
        return ResponseEntity
                .ok(userRegistrationMapper.toRegistrationResponseDto(registeredUser, emailVerificationRequired));
    }

}