package com.user.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private List<Phone> phones;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private OffsetDateTime lastLogin;
    private String token;
    private boolean active;
}