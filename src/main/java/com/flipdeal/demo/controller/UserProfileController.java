package com.flipdeal.demo.controller;

import com.flipdeal.demo.dto.UserProfileDto;
import com.flipdeal.demo.mapper.UserMapper;
import com.flipdeal.demo.userservice.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getUserProfile(final Authentication authentication) {
        log.info("GET /api/user/me - getUserProfile called for username: {}", authentication.getName());
        final var user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(userMapper.toUserProfileDto(user));
    }
}