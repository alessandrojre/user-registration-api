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

    private final UserResponseMapper userResponseMapper = new UserResponseMapper();

    @Test
    void toResponse_shouldMaskEmailAndPhoneAndMapAllFields() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        User user = new User(
                userId,
                "User Test",
                "user@gmail.com",
                "hashedPassword",
                List.of(new Phone("1234567", "1", "57")),
                now, now, now,
                "jwt-token",
                true
        );

        UserResponse response = userResponseMapper.toResponse(user);

        // Then
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo("use***@gmail.com");
        assertThat(response.getPhones()).hasSize(1);
        assertThat(response.getPhones().get(0).getNumber()).isEqualTo("****567");
        assertThat(response.getPhones().get(0).getCityCode()).isEqualTo("1");
        assertThat(response.getPhones().get(0).getCountryCode()).isEqualTo("57");
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.isActive()).isTrue();
    }

    @Test
    void toResponse_shouldHandleShortPhoneNumbersAndShortLocalPartEmails() {
        // Given
        UUID userId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        User user = new User(
                userId,
                "Alessandro R",
                "alessandro@gmail.com",
                "hashedPassword",
                List.of(new Phone("955434211", "01", "51")),
                now, now, now,
                "jwt-token",
                true
        );

        // When
        UserResponse response = userResponseMapper.toResponse(user);

        // Then
        assertThat(response.getEmail()).isEqualTo("ale***@gmail.com");
        assertThat(response.getPhones()).hasSize(1);
        assertThat(response.getPhones().get(0).getNumber()).isEqualTo("******211");
        assertThat(response.getPhones().get(0).getCityCode()).isEqualTo("01");
        assertThat(response.getPhones().get(0).getCountryCode()).isEqualTo("51");
    }
}
