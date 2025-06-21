package com.flipdeal.demo.mapper;

import com.flipdeal.demo.dto.UserProfileDto;
import com.flipdeal.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileDto toUserProfileDto(final User user) {
        return new UserProfileDto(user.getEmail(), user.getUsername(), user.isEmailVerified());
    }

}