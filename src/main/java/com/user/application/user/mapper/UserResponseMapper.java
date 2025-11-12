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
                        .map(p -> new UserResponse.PhoneResponse(
                                maskPhoneNumber(p.getNumber()),
                                p.getCityCode(),
                                p.getCountryCode()))
                        .collect(Collectors.toList()),
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.getToken(),
                user.isActive()
        );
    }

    private String maskEmail(String email) {
        if (email == null) return "***";
        int atIndex = email.indexOf('@');
        if (atIndex < 0) return "***";
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex); // incluye '@'
        String visible = local.length() <= 2 ? local.substring(0, 1)
                : local.substring(0, Math.min(3, local.length()));
        return visible + "***" + domain;
    }

    private String maskPhoneNumber(String number) {
        if (number == null) return "***";
        String digits = number.replaceAll("\\D", "");
        if (digits.length() < 4) return "***";
        int visibleDigits = 3;
        String hiddenPart = "*".repeat(Math.max(0, digits.length() - visibleDigits));
        return hiddenPart + digits.substring(digits.length() - visibleDigits);
    }
}
