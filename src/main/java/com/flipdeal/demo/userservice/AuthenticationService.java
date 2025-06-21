package com.flipdeal.demo.userservice;

import com.flipdeal.demo.dto.AuthenticationRequestDto;
import com.flipdeal.demo.dto.AuthenticationResponseDto;
import com.flipdeal.demo.entity.RefreshToken;
import com.flipdeal.demo.repository.RefreshTokenRepository;
import com.flipdeal.demo.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    @Value("${jwt.refresh-token-ttl}")
    private final Duration refreshTokenTtl;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticationResponseDto authenticate(final AuthenticationRequestDto request) {
        log.info("AuthenticationService: authenticate called for username: {}", request.username());
        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.username(),
                request.password());
        final var authentication = authenticationManager.authenticate(authToken);
        log.info("User authenticated: {}", request.username());
        final var accessToken = jwtService.generateToken(request.username());
        log.info("Access token generated for username: {}", request.username());
        final var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    log.error("User with username [{}] not found", request.username());
                    return new UsernameNotFoundException(
                            "User with username [%s] not found".formatted(request.username()));
                });
        var refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plus(refreshTokenTtl));
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token generated for user: {}", user.getUsername());
        return new AuthenticationResponseDto(accessToken, refreshToken.getId());
    }

    public AuthenticationResponseDto refreshToken(UUID refreshToken) {
        log.info("AuthenticationService: refreshToken called for token: {}", refreshToken);
        final var refreshTokenEntity = refreshTokenRepository.findByIdAndExpiresAtAfter(refreshToken, Instant.now())
                .orElseThrow(() -> {
                    log.error("Invalid or expired refresh token: {}", refreshToken);
                    return new BadCredentialsException("Invalid or expired refresh token");
                });
        final var newAccessToken = jwtService.generateToken(refreshTokenEntity.getUser().getUsername());
        log.info("New access token generated for user: {}", refreshTokenEntity.getUser().getUsername());
        return new AuthenticationResponseDto(newAccessToken, refreshToken);
    }

    public void revokeRefreshToken(UUID refreshToken) {
        log.info("AuthenticationService: revokeRefreshToken called for token: {}", refreshToken);
        refreshTokenRepository.deleteById(refreshToken);
        log.info("Refresh token revoked: {}", refreshToken);
    }
}