package com.user.infrastructure.persistence.jpa.mapper;

import com.user.domain.user.Phone;
import com.user.domain.user.User;
import com.user.infrastructure.persistence.jpa.model.PhoneEntity;
import com.user.infrastructure.persistence.jpa.model.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityMapperTest {

    private final UserEntityMapper mapper = new UserEntityMapper();

    @Test
    void toEntity_shouldMapAllFields() {

        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        List<Phone> phones = List.of(new Phone("1234567", "1", "57"));
        User domain = new User(
                id,
                "UserTest",
                "test@gmail.com",
                "hash",
                phones,
                now,
                now,
                now,
                "jwt",
                true
        );

        UserEntity entity = mapper.toEntity(domain);
        List<PhoneEntity> phoneEntities = mapper.toPhoneEntities(entity, phones);

        assertThat(phoneEntities)
                .hasSize(1)
                .first()
                .satisfies(phone -> {
                    assertThat(phone.getNumber()).isEqualTo("1234567");
                    assertThat(phone.getCityCode()).isEqualTo("1");
                    assertThat(phone.getCountryCode()).isEqualTo("57");
                });

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void toPhoneEntities_shouldReturnEmptyList_whenPhonesNullOrEmpty() {
        UserEntity userEntity = new UserEntity();

        assertThat(mapper.toPhoneEntities(userEntity, null)).isEmpty();
        assertThat(mapper.toPhoneEntities(userEntity, List.of())).isEmpty();
    }


    @Test
    void toDomain_shouldMapAllFields() {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setName("UserTest");
        entity.setEmail("test@gmail.com");
        entity.setPassword("hash");
        entity.setCreated(now);
        entity.setModified(now);
        entity.setLastLogin(now);
        entity.setToken("jwt");
        entity.setActive(true);

        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setNumber("1234567");
        phoneEntity.setCityCode("1");
        phoneEntity.setCountryCode("57");

        User domain = mapper.toDomain(entity, List.of(phoneEntity));

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getPhones()).hasSize(1);
        assertThat(domain.getToken()).isEqualTo("jwt");
    }
}
