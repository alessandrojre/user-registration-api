package com.user.application.user.validator;

import com.user.application.user.dto.UserRequest;
import com.user.domain.user.port.UserRepositoryPort;
import com.user.exception.BusinessException;

public class UserValidator {

    private final String emailRegex;
    private final String passwordRegex;
    private final UserRepositoryPort userRepositoryPort;

    public UserValidator(String emailRegex, String passwordRegex, UserRepositoryPort userRepositoryPort) {
        this.emailRegex = emailRegex;
        this.passwordRegex = passwordRegex;
        this.userRepositoryPort = userRepositoryPort;
    }

    public void validate(UserRequest userRequest) {
        if (!userRequest.getEmail().matches(emailRegex))
            throw new BusinessException("El correo no tiene un formato válido");

        if (!userRequest.getPassword().matches(passwordRegex))
            throw new BusinessException("La clave no cumple la política de seguridad");

        if (userRepositoryPort.existsByEmail(userRequest.getEmail()))
            throw new BusinessException("El correo ya se encuentra registrado");
    }
}
