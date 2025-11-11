package com.user.application.user.service;

import com.user.application.user.dto.UserRequest;
import com.user.application.user.dto.UserResponse;
import com.user.application.user.factory.UserFactory;
import com.user.application.user.mapper.UserResponseMapper;
import com.user.application.user.validator.UserValidator;
import com.user.domain.user.Phone;
import com.user.domain.user.User;
import com.user.domain.user.port.UserRepositoryPort;
import com.user.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserFactory userFactory;
    @Mock
    private UserResponseMapper userResponseMapper;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepositoryPort,
                userValidator,
                userFactory,
                userResponseMapper
        );
    }

    private UserRequest buildValidRequest() {
        UserRequest.PhoneRequest request = new UserRequest.PhoneRequest();
        request.setNumber("1234567");
        request.setCityCode("1");
        request.setCountryCode("57");

        UserRequest req = new UserRequest();
        req.setName("UserTest");
        req.setEmail("usertest@gmail.com");
        req.setPassword("hashed.password");
        req.setPhones(List.of(request));
        return req;
    }

    private User buildDomainUser(UUID id) {
        OffsetDateTime now = OffsetDateTime.now();
        return new User(
                id,
                "Juan UserTest",
                "usertest@gmail.com",
                "hashed.password",
                List.of(new Phone("1234567", "1", "57")),
                now, now, now,
                "jwt-token",
                true
        );
    }

    private UserResponse buildResponseFrom(User saved) {
        UserResponse.PhoneResponse pr = new UserResponse.PhoneResponse(
                "1234567", "1", "57"
        );
        return new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                List.of(pr),
                saved.getCreated(),
                saved.getModified(),
                saved.getLastLogin(),
                saved.getToken(),
                saved.isActive()
        );
    }

    @Test
    void register_shouldCreateUserSuccessfully() {
        // Given
        UserRequest request = buildValidRequest();
        UUID generatedId = UUID.randomUUID();
        User domainToPersist = buildDomainUser(generatedId);
        User persisted = domainToPersist;
        UserResponse expectedResponse = buildResponseFrom(persisted);


        when(userFactory.createUser(request)).thenReturn(domainToPersist);

        when(userRepositoryPort.save(domainToPersist)).thenReturn(persisted);

        when(userResponseMapper.toResponse(persisted)).thenReturn(expectedResponse);

        // Act
        UserResponse out = userService.register(request);

        // Then
        assertThat(out).usingRecursiveComparison().isEqualTo(expectedResponse);

        InOrder inOrder = inOrder(userValidator, userFactory, userRepositoryPort, userResponseMapper);
        inOrder.verify(userValidator).validate(request);
        inOrder.verify(userFactory).createUser(request);
        inOrder.verify(userRepositoryPort).save(userCaptor.capture());
        inOrder.verify(userResponseMapper).toResponse(persisted);
        inOrder.verifyNoMoreInteractions();

        User captured = userCaptor.getValue();
        assertThat(captured.getEmail()).isEqualTo("usertest@gmail.com");
        assertThat(captured.getPhones()).hasSize(1);
    }

    @Test
    void register_shouldStopWhenValidatorFails() {
        // Given
        UserRequest request = buildValidRequest();
        doThrow(new BusinessException("Datos inválidos")).when(userValidator).validate(request);

        // When / Then
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Datos inválidos");

        verify(userValidator).validate(request);
        verifyNoInteractions(userFactory, userRepositoryPort, userResponseMapper);
    }

    @Test
    void register_shouldPropagateRepositoryException() {
        // Given
        UserRequest request = buildValidRequest();
        User domainToPersist = buildDomainUser(UUID.randomUUID());

        when(userFactory.createUser(request)).thenReturn(domainToPersist);
        when(userRepositoryPort.save(domainToPersist))
                .thenThrow(new RuntimeException("Fallo de persistencia"));

        // When / Then
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Fallo de persistencia");

        verify(userValidator).validate(request);
        verify(userFactory).createUser(request);
        verify(userRepositoryPort).save(domainToPersist);
        verifyNoMoreInteractions(userRepositoryPort);
        verifyNoInteractions(userResponseMapper);
    }

    @Test
    void register_shouldCallCollaboratorsInOrder() {
        // Given
        UserRequest request = buildValidRequest();
        User domainToPersist = buildDomainUser(UUID.randomUUID());
        when(userFactory.createUser(request)).thenReturn(domainToPersist);
        when(userRepositoryPort.save(domainToPersist)).thenReturn(domainToPersist);
        when(userResponseMapper.toResponse(domainToPersist)).thenReturn(buildResponseFrom(domainToPersist));

        // Act
        userService.register(request);

        // Then
        InOrder inOrder = inOrder(userValidator, userFactory, userRepositoryPort, userResponseMapper);
        inOrder.verify(userValidator).validate(request);
        inOrder.verify(userFactory).createUser(request);
        inOrder.verify(userRepositoryPort).save(any(User.class));
        inOrder.verify(userResponseMapper).toResponse(any(User.class));
        inOrder.verifyNoMoreInteractions();
    }
}
