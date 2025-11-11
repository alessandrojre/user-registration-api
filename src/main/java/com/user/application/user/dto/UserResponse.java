package com.user.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private List<PhoneResponse> phones;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private OffsetDateTime lastLogin;
    private String token;
    private boolean active;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneResponse {
        private String number;
        private String cityCode;
        private String countryCode;
    }

}