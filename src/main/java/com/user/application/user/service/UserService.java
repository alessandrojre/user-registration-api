package com.user.application.user.service;


import com.user.application.user.dto.UserRequest;
import com.user.application.user.dto.UserResponse;
import com.user.application.user.factory.UserFactory;
import com.user.application.user.mapper.UserResponseMapper;
import com.user.application.user.usecase.UserUseCase;
import com.user.application.user.validator.UserValidator;
import com.user.domain.user.User;
import com.user.domain.user.port.UserRepositoryPort;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    private final UserValidator userValidator;
    private final UserFactory userFactory;
    private final UserResponseMapper userResponseMapper;

    public UserService(UserRepositoryPort userRepositoryPort,
                       UserValidator userValidator,
                       UserFactory userFactory,
                       UserResponseMapper userResponseMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userValidator = userValidator;
        this.userFactory = userFactory;
        this.userResponseMapper = userResponseMapper;
    }

    @Override
    public UserResponse register(UserRequest userRequest) {
        userValidator.validate(userRequest);
        User user = userFactory.createUser(userRequest);
        User savedUser = userRepositoryPort.save(user);
        log.info("Usuario registrado exitosamente con ID: {}", savedUser.getId());
        return userResponseMapper.toResponse(savedUser);
    }
}