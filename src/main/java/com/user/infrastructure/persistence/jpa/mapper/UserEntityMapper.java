package com.user.infrastructure.persistence.jpa.mapper;

import com.user.domain.user.Phone;
import com.user.domain.user.User;
import com.user.infrastructure.persistence.jpa.model.PhoneEntity;
import com.user.infrastructure.persistence.jpa.model.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        if (user == null) return null;

        UserEntity e = new UserEntity();
        e.setId(user.getId());
        e.setName(user.getName());
        e.setEmail(user.getEmail());
        e.setPassword(user.getPassword());
        e.setCreated(user.getCreated());
        e.setModified(user.getModified());
        e.setLastLogin(user.getLastLogin());
        e.setToken(user.getToken());
        e.setActive(user.isActive());
        return e;
    }

    public User toDomain(UserEntity userEntity, List<PhoneEntity> phoneEntities) {
        if (userEntity == null) return null;

        List<Phone> phones = phoneEntities == null ? List.of()
                : phoneEntities.stream()
                .filter(Objects::nonNull)
                .map(this::toDomainPhone)
                .toList();

        return new User(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                phones,
                userEntity.getCreated(),
                userEntity.getModified(),
                userEntity.getLastLogin(),
                userEntity.getToken(),
                userEntity.isActive()
        );
    }

    public List<PhoneEntity> toPhoneEntities(UserEntity owner, List<Phone> phones) {
        if (owner == null || phones == null || phones.isEmpty()) return List.of();

        return phones.stream()
                .filter(Objects::nonNull)
                .map(p -> {
                    PhoneEntity pe = new PhoneEntity();
                    pe.setNumber(p.getNumber());
                    pe.setCityCode(p.getCityCode());
                    pe.setCountryCode(p.getCountryCode());
                    pe.setUser(owner); // back-reference
                    return pe;
                })
                .toList();
    }

    private Phone toDomainPhone(PhoneEntity pe) {
        return new Phone(pe.getNumber(), pe.getCityCode(), pe.getCountryCode());
    }
}