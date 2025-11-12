package com.user.application.user.validator;

import com.user.application.user.dto.UserRequest;
import com.user.domain.user.port.UserRepositoryPort;
import com.user.exception.BusinessException;

import java.util.regex.Pattern;

public class UserValidator {

    private final Pattern emailPattern;
    private final Pattern passwordPattern;
    private final UserRepositoryPort userRepositoryPort;

    public UserValidator(String emailRegex, String passwordRegex, UserRepositoryPort userRepositoryPort) {
        this.emailPattern = Pattern.compile(emailRegex);
        this.passwordPattern = Pattern.compile(passwordRegex);
        this.userRepositoryPort = userRepositoryPort;
    }

    public void validate(UserRequest userRequest) {
        if (userRequest == null) throw new BusinessException("Solicitud inválida");

        String email = userRequest.getEmail();
        String password = userRequest.getPassword();

        if (email == null || !emailPattern.matcher(email).matches())
            throw new BusinessException("El correo no tiene un formato válido");

        if (password == null || !passwordPattern.matcher(password).matches())
            throw new BusinessException("La clave no cumple la política de seguridad");

        if (userRepositoryPort.existsByEmail(email))
            throw new BusinessException("El correo ya se encuentra registrado");
    }
}
