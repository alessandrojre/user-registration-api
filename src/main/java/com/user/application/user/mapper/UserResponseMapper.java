package com.user.application.user.mapper;

import com.user.application.user.dto.UserResponse;
import com.user.domain.user.User;

import java.util.stream.Collectors;

public class UserResponseMapper {

    public UserResponseMapper() {}

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhones().stream()
                        .map(p ->
                                new UserResponse.PhoneResponse(p.getNumber(), p.getCityCode(), p.getCountryCode()))
                        .collect(Collectors.toList()),
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.getToken(),
                user.isActive()
        );
    }
}