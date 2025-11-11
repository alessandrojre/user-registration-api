package com.user.application.user.factory;

import com.user.application.user.dto.UserRequest;
import com.user.domain.auth.TokenData;
import com.user.domain.auth.port.PasswordEncoderPort;
import com.user.domain.auth.port.TokenProviderPort;
import com.user.domain.user.Phone;
import com.user.domain.user.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserFactory {

    private final TokenProviderPort tokenProviderPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final long tokenExpirationMinutes;

    public UserFactory(TokenProviderPort tokenProviderPort,
                       PasswordEncoderPort passwordEncoderPort,
                       long tokenExpirationMinutes) {
        this.tokenProviderPort = tokenProviderPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenExpirationMinutes = tokenExpirationMinutes;
    }

    public User createUser(UserRequest request) {
        UUID userId = UUID.randomUUID();
        String passwordHash = encodePassword(request.getPassword());
        String token = generateToken(userId, request.getEmail());
        List<Phone> phones = mapPhones(request);
        OffsetDateTime now = OffsetDateTime.now();

        return new User(
                userId, request.getName(), request.getEmail(), passwordHash,
                phones, now, now, now, token, true
        );
    }

    private String encodePassword(String password) {
        return passwordEncoderPort.encode(password);
    }

    private String generateToken(UUID userId, String email) {
        long expirationMillis = System.currentTimeMillis() + tokenExpirationMinutes * 60_000;
        return tokenProviderPort.generate(new TokenData(userId, email, expirationMillis));
    }

    private List<Phone> mapPhones(UserRequest request) {
        return request.getPhones().stream()
                .map(p -> new Phone(p.getNumber(), p.getCityCode(), p.getCountryCode()))
                .collect(Collectors.toList());
    }
}