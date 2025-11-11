package com.user.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenData {
    private UUID userId;
    private String email;
    private long expMillis;
}