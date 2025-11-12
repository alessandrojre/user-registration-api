package com.user.infrastructure.config;

import com.user.application.user.factory.UserFactory;
import com.user.application.user.mapper.UserResponseMapper;
import com.user.application.user.service.UserService;
import com.user.application.user.usecase.UserUseCase;
import com.user.application.user.validator.UserValidator;
import com.user.domain.auth.port.PasswordEncoderPort;
import com.user.domain.auth.port.TokenProviderPort;
import com.user.domain.user.port.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BeanConfig {

    @Bean
    public UserUseCase registerUserUseCase(UserRepositoryPort userRepository,
                                           TokenProviderPort tokenProvider,
                                           PasswordEncoderPort passwordEncoder,
                                           RegexProperties regexProps,
                                           SecurityProperties securityProps) {



        UserValidator validator = new UserValidator(
                regexProps.getEmailRegex(),
                regexProps.getPasswordRegex(),
                userRepository
        );

        UserFactory factory = new UserFactory(
                tokenProvider,
                passwordEncoder,
                securityProps.getExpirationMinutes()
        );

        UserResponseMapper userResponseMapper = new UserResponseMapper();

        return new UserService(userRepository, validator, factory, userResponseMapper);
    }
}