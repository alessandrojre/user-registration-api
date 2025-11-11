package com.user.domain.auth.port;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
}