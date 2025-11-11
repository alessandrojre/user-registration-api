package com.user.application.user.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private List<PhoneRequest> phones;

    @Data
    public static class PhoneRequest {
        @NotBlank
        private String number;
        @NotBlank
        private String cityCode;
        @NotBlank
        private String countryCode;
    }
}