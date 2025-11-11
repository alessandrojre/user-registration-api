package com.user.infrastructure.api.user;

import com.user.application.user.dto.UserRequest;
import com.user.application.user.dto.UserResponse;
import com.user.application.user.usecase.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase registerUser;


    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest userRequest) {
        var response = registerUser.register(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
