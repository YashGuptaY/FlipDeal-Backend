package com.flipdeal.demo.controller;

import com.flipdeal.demo.dto.AuthenticationRequestDto;
import com.flipdeal.demo.dto.AuthenticationResponseDto;
import com.flipdeal.demo.userservice.AuthenticationService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @Valid @RequestBody final AuthenticationRequestDto authenticationRequestDto) {
        log.info("POST /api/auth/login called for username: {}", authenticationRequestDto.username());
        AuthenticationResponseDto response = authenticationService.authenticate(authenticationRequestDto);
        log.info("Authentication successful for username: {}", authenticationRequestDto.username());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(@RequestParam UUID refreshToken) {
        log.info("POST /api/auth/refresh-token called for refreshToken: {}", refreshToken);
        AuthenticationResponseDto response = authenticationService.refreshToken(refreshToken);
        log.info("Refresh token successful for token: {}", refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> revokeToken(@RequestParam UUID refreshToken) {
        log.info("POST /api/auth/logout called for refreshToken: {}", refreshToken);
        authenticationService.revokeRefreshToken(refreshToken);
        log.info("Refresh token revoked for token: {}", refreshToken);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email) {
        return ResponseEntity.ok(email != null);
    }
}