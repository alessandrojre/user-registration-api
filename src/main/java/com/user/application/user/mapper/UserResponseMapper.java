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
                maskEmail(user.getEmail()),
                user.getPhones().stream()
                        .map(p ->
                                new UserResponse
                                        .PhoneResponse(maskPhoneNumber(p.getNumber()), p.getCityCode(), p.getCountryCode()))
                        .collect(Collectors.toList()),
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.getToken(),
                user.isActive()
        );
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***";
        }
        return email.substring(0, Math.min(3, atIndex)) + "***" + email.substring(atIndex);
    }
    private String maskPhoneNumber(String number) {
        if (number == null || number.length() < 4) {
            return "***";
        }
        int visibleDigits = 3;
        String hiddenPart = "*".repeat(Math.max(0, number.length() - visibleDigits));
        return hiddenPart + number.substring(number.length() - visibleDigits);
    }

}