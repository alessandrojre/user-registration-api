package com.user.infrastructure.persistence.jpa.mapper;

import com.user.domain.user.Phone;
import com.user.domain.user.User;
import com.user.infrastructure.persistence.jpa.model.PhoneEntity;
import com.user.infrastructure.persistence.jpa.model.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setCreated(user.getCreated());
        userEntity.setModified(user.getModified());
        userEntity.setLastLogin(user.getLastLogin());
        userEntity.setToken(user.getToken());
        userEntity.setActive(user.isActive());
        return userEntity;
    }

    public List<PhoneEntity> toPhoneEntities(UserEntity persistedUserEntity, List<Phone> phones) {
        if (phones == null || phones.isEmpty()) {
            return List.of();
        }

        return phones.stream()
                .map(phone -> {
                    PhoneEntity phoneEntity = new PhoneEntity();
                    phoneEntity.setNumber(phone.getNumber());
                    phoneEntity.setCityCode(phone.getCityCode());
                    phoneEntity.setCountryCode(phone.getCountryCode());
                    phoneEntity.setUser(persistedUserEntity);
                    return phoneEntity;
                })
                .collect(Collectors.toList());
    }

    public User toDomain(UserEntity userEntity, List<Phone> phones) {
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
}