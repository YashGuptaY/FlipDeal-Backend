package com.flipdeal.demo.controller;

import com.flipdeal.demo.dto.ResetPasswordRequest;
import com.flipdeal.demo.userservice.ProfileServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationController {

    private final ProfileServiceImpl profileServiceImpl;

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerificationLink(@RequestParam String email) {
        log.info("POST /api/auth/email/resend-verification - resendVerificationLink called for email: {}", email);
        profileServiceImpl.sendResetOtp(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-otp")
    public void verifyEmail(@RequestBody Map<String, Object> request,
    								@RequestParam("uid") UUID encryptedUserId) {
        if (request.get("otp").toString() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing details");
        }

        try {
           profileServiceImpl.verifyOtp(encryptedUserId, request.get("otp").toString());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            profileServiceImpl.resetPassword(request.getUserId(), request.getOtp(), request.getNewPassword());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}