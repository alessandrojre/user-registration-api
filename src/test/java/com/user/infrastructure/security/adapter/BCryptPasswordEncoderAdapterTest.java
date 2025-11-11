package com.user.infrastructure.security.adapter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordEncoderAdapterTest {

    @Test
    void encode_and_matches_shouldWork() {
        BCryptPasswordEncoderAdapter adapter = new BCryptPasswordEncoderAdapter();

        String raw = "passwordTest";
        String hash = adapter.encode(raw);

        assertThat(hash).isNotBlank();
    }
}
