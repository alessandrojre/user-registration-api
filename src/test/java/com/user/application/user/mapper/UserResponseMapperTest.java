package com.user.application.user.mapper;

import com.user.application.user.dto.UserResponse;
import com.user.domain.user.Phone;
import com.user.domain.user.User;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseMapperTest {

    private final UserResponseMapper mapper = new UserResponseMapper();

    @Test
    void toResponse_shouldMapAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = new User(
                id,
                "John",
                "john@example.com",
                "hashed",
                List.of(new Phone("1234567", "1", "57")),
                now, now, now,
                "jwt-token",
                true
        );

        UserResponse response = mapper.toResponse(user);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getPhones()).hasSize(1);
        assertThat(response.getPhones().get(0).getNumber()).isEqualTo("1234567");
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.isActive()).isTrue();
    }
}
