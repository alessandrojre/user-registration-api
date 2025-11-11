package com.user.application.user.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class UserRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private List<@Valid PhoneRequest> phones;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhoneRequest {

        @NotBlank(message = "El número no puede estar vacío")
        @Pattern(regexp = "^[0-9]+$", message = "El número debe contener solo dígitos")
        private String number;

        @NotBlank(message = "El código de ciudad no puede estar vacío")
        @Pattern(regexp = "^[0-9]+$", message = "El código de ciudad debe contener solo dígitos")
        private String cityCode;

        @NotBlank(message = "El código de país no puede estar vacío")
        @Pattern(regexp = "^[0-9]+$", message = "El código de país debe contener solo dígitos")
        private String countryCode;
    }
}