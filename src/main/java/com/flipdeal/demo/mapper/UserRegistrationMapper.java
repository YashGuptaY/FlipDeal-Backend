package com.flipdeal.demo.mapper;

import com.flipdeal.demo.dto.RegistrationRequestDto;
import com.flipdeal.demo.dto.RegistrationResponseDto;
import com.flipdeal.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {

    public User toEntity(RegistrationRequestDto registrationRequestDto) {
        final var user = new User();
        user.setUsername(registrationRequestDto.username());
        user.setEmail(registrationRequestDto.email());
        //user.setUsername(registrationRequestDto.username());
        user.setPassword(registrationRequestDto.password());

        return user;
    }

    public RegistrationResponseDto toRegistrationResponseDto(final User user, final boolean emailVerificationRequired) {
        return new RegistrationResponseDto(user.getUsername(), user.getEmail(), emailVerificationRequired);
    }

}