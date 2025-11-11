package com.user.infrastructure.security.adapter;

import com.user.domain.auth.TokenData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceAdapterTest {

    @Test
    void generate_and_parse_and_isValid_shouldWork() {

        String secret = "0123456789ABCDEF0123456789ABCDEF";
        System.setProperty("JWT_SECRET", secret);
        JwtServiceAdapter adapter = new JwtServiceAdapter(secret);

        TokenData data = new TokenData(UUID.randomUUID(), "mail@gmail.com",
                System.currentTimeMillis() + 3_600_000);

        String token = adapter.generate(data);

        assertThat(adapter.isValid(token)).isTrue();
        TokenData parsed = adapter.parse(token);
        assertThat(parsed.getEmail()).isEqualTo("mail@gmail.com");
    }
}
