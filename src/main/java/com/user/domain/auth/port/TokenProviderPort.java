package com.user.domain.auth.port;

import com.user.domain.auth.TokenData;

public interface TokenProviderPort {
    String generate(TokenData data);
    boolean isValid(String token);
    TokenData parse(String token);
}