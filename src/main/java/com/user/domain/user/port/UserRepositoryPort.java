package com.user.domain.user.port;

import com.user.domain.user.User;

public interface UserRepositoryPort {

    boolean existsByEmail(String email);
    User save(User user);
}