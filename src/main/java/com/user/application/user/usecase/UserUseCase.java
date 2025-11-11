package com.user.application.user.usecase;

import com.user.application.user.dto.UserRequest;
import com.user.application.user.dto.UserResponse;

public interface UserUseCase {
    UserResponse register(UserRequest req);
}