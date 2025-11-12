package com.user.domain.user.port;

import com.user.domain.user.User;

import java.util.Optional;

public interface UserRepositoryPort {

    boolean existsByEmail(String email);
    User save(User user);
    Optional<User> findByEmail(String email);
}