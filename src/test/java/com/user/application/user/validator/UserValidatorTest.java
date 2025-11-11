package com.user.application.user.validator;

import com.user.application.user.dto.UserRequest;
import com.user.domain.user.port.UserRepositoryPort;
import com.user.exception.BusinessException;
import com.user.infrastructure.config.RegexProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserValidatorTest {

    private UserValidator validator;
    private UserRepositoryPort userRepositoryPort;

    @BeforeEach
    void setUp() {
        RegexProperties regexProps = new RegexProperties();
        regexProps.setEmailRegex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        regexProps.setPasswordRegex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{8,}$");

        userRepositoryPort = mock(UserRepositoryPort.class);

        validator = new UserValidator(regexProps.getEmailRegex(),
                regexProps.getPasswordRegex(),
                userRepositoryPort);
    }

    @Test
    void validate_shouldAcceptValidData() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("Alessandro");
        userRequest.setEmail("alessandro@gmail.com");
        userRequest.setPassword("Password@123");

        when(userRepositoryPort.existsByEmail("alessandro@gmail.com")).thenReturn(false);

        assertThatCode(() -> validator.validate(userRequest)).doesNotThrowAnyException();
    }

    @Test
    void validate_shouldFailOnEmailFormat() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("bad-email");
        userRequest.setPassword("Password.123");

        assertThatThrownBy(() -> validator.validate(userRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("El correo no tiene un formato válido");
    }

    @Test
    void validate_shouldFailOnPasswordPolicy() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("ok@mail.com");
        userRequest.setPassword("password");

        assertThatThrownBy(() -> validator.validate(userRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("clave");
    }

    @Test
    void validate_shouldFailWhenEmailAlreadyExists() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("exists@mail.com");
        userRequest.setPassword("Password.123");

        when(userRepositoryPort.existsByEmail("exists@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> validator.validate(userRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("El correo ya se encuentra registrado");
    }
}
