package com.user.infrastructure.persistence.jpa.repository;

import com.user.infrastructure.persistence.jpa.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);
}