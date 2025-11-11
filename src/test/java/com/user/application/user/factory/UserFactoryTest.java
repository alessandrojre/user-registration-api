package com.user.application.user.factory;

import com.user.application.user.dto.UserRequest;
import com.user.domain.auth.TokenData;
import com.user.domain.auth.port.PasswordEncoderPort;
import com.user.domain.auth.port.TokenProviderPort;
import com.user.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFactoryTest {

    @Mock
    private TokenProviderPort tokenProviderPort;
    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    private UserFactory buildFactory(long tokenExpirationMinutes) {
        return new UserFactory(tokenProviderPort, passwordEncoderPort, tokenExpirationMinutes);
    }

    @Test
    void createUser_shouldBuildDomainUserAndCallPorts() {

        UserRequest request = new UserRequest();
        request.setName("Alessandro");
        request.setEmail("alessandro@gmail.com");
        request.setPassword("secret123");
        request.setPhones(List.of(new UserRequest.PhoneRequest("1234567", "1", "57")));

        when(passwordEncoderPort.encode("secret123")).thenReturn("HASHED");
        ArgumentCaptor<TokenData> tokenDataCaptor = ArgumentCaptor.forClass(TokenData.class);
        when(tokenProviderPort.generate(any(TokenData.class))).thenReturn("JWT-TOKEN");

        UserFactory factory = buildFactory(1);

        OffsetDateTime before = OffsetDateTime.now();
        User user = factory.createUser(request);
        OffsetDateTime after = OffsetDateTime.now();

        verify(passwordEncoderPort, times(1)).encode("secret123");
        verify(tokenProviderPort, times(1)).generate(tokenDataCaptor.capture());

        TokenData sent = tokenDataCaptor.getValue();
        assertThat(sent.getEmail()).isEqualTo("alessandro@gmail.com");
        assertThat(sent.getUserId()).isEqualTo(user.getId());

        assertThat(user.getId()).isNotNull();
        assertThat(user.getPassword()).isEqualTo("HASHED");
        assertThat(user.getToken()).isEqualTo("JWT-TOKEN");
        assertThat(user.isActive()).isTrue();

        assertThat(user.getCreated()).isBetween(
                before.minusSeconds(1),
                after.plusSeconds(1));
        assertThat(user.getModified()).isBetween(
                before.minusSeconds(1),
                after.plusSeconds(1));
        assertThat(user.getLastLogin()).isBetween(
                before.minusSeconds(1),
                after.plusSeconds(1));

        assertThat(user.getPhones()).hasSize(1);
        assertThat(user.getPhones().get(0).getNumber()).isEqualTo("1234567");
        assertThat(user.getPhones().get(0).getCityCode()).isEqualTo("1");
        assertThat(user.getPhones().get(0).getCountryCode()).isEqualTo("57");
    }

    @Test
    void createUser_shouldAllowEmptyPhones() {
        UserRequest request = new UserRequest();
        request.setName("Alessandro");
        request.setEmail("alessandro@gmail.com");
        request.setPassword("secret123");
        request.setPhones(List.of());

        when(passwordEncoderPort.encode("secret123")).thenReturn("H");
        when(tokenProviderPort.generate(any(TokenData.class))).thenReturn("T");

        UserFactory factory = buildFactory(5);

        User user = factory.createUser(request);

        assertThat(user.getPhones()).isEmpty();
    }
}
