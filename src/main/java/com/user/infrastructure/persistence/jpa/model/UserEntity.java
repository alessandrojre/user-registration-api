package com.user.infrastructure.persistence.jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
public class UserEntity {
    @Id
    private UUID id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private OffsetDateTime lastLogin;
    private String token;
    private boolean active;

}