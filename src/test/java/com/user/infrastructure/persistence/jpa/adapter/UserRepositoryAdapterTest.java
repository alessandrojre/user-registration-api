package com.user.infrastructure.persistence.jpa.adapter;

import com.user.domain.user.Phone;
import com.user.domain.user.User;
import com.user.domain.user.port.UserRepositoryPort;
import com.user.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import com.user.infrastructure.persistence.jpa.model.PhoneEntity;
import com.user.infrastructure.persistence.jpa.model.UserEntity;
import com.user.infrastructure.persistence.jpa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @Mock
    private EntityManager entityManager;

    private UserRepositoryPort userRepositoryAdapter;

    @BeforeEach
    void setUp() {
        userRepositoryAdapter = new UserRepositoryAdapter(userRepository, userEntityMapper);

        try {
            Field field = UserRepositoryAdapter.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(userRepositoryAdapter, entityManager);
        } catch (Exception ignored) {
        }
    }

    private User buildDomainUser(UUID userId) {
        OffsetDateTime now = OffsetDateTime.now();
        List<Phone> phoneList = List.of(new Phone("1234567", "1", "57"));
        return new User(
                userId,
                "Alessandro Riega",
                "alessandro.riega@gmail.com",
                "password.123",
                phoneList,
                now,
                now,
                now,
                "jwt-token",
                true
        );
    }

    private UserEntity buildUserEntity(UUID userId) {
        OffsetDateTime now = OffsetDateTime.now();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("Alessandro Riega");
        userEntity.setEmail("alessandro.riega@gmail.com");
        userEntity.setPassword("password.123");
        userEntity.setCreated(now);
        userEntity.setModified(now);
        userEntity.setLastLogin(now);
        userEntity.setToken("jwt-token");
        userEntity.setActive(true);
        return userEntity;
    }

    private PhoneEntity buildPhoneEntity(UserEntity persistedUserEntity) {
        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setNumber("1234567");
        phoneEntity.setCityCode("1");
        phoneEntity.setCountryCode("57");
        phoneEntity.setUser(persistedUserEntity);
        return phoneEntity;
    }


    @Test
    void save_shouldMapPersistPhonesAndReturnDomain() {
        // given
        UUID userId = UUID.randomUUID();
        User domainUser = buildDomainUser(userId);
        UserEntity userEntity = buildUserEntity(userId);
        UserEntity persistedUserEntity = buildUserEntity(userId);
        List<PhoneEntity> phoneEntities = List.of(buildPhoneEntity(persistedUserEntity));
        User expectedDomainUser = buildDomainUser(userId);

        when(userEntityMapper.toEntity(domainUser)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(persistedUserEntity);
        when(userEntityMapper.toPhoneEntities(persistedUserEntity, domainUser.getPhones()))
                .thenReturn(phoneEntities);
        when(userEntityMapper.toDomain(persistedUserEntity, domainUser.getPhones()))
                .thenReturn(expectedDomainUser);

        // act
        User result = userRepositoryAdapter.save(domainUser);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedDomainUser);

        InOrder inOrder = inOrder(userEntityMapper, userRepository, entityManager);
        inOrder.verify(userEntityMapper).toEntity(domainUser);
        inOrder.verify(userRepository).save(userEntity);
        inOrder.verify(userEntityMapper).toPhoneEntities(persistedUserEntity, domainUser.getPhones());
        verify(entityManager, times(phoneEntities.size())).persist(any(PhoneEntity.class));
        inOrder.verify(userEntityMapper).toDomain(persistedUserEntity, domainUser.getPhones());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void save_shouldNotPersistPhones_whenNoPhonesReturnedByMapper() {
        // given
        UUID userId = UUID.randomUUID();
        User domainUser = buildDomainUser(userId);
        UserEntity userEntity = buildUserEntity(userId);
        UserEntity persistedUserEntity = buildUserEntity(userId);

        when(userEntityMapper.toEntity(domainUser)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(persistedUserEntity);
        when(userEntityMapper.toPhoneEntities(persistedUserEntity, domainUser.getPhones()))
                .thenReturn(List.of());
        when(userEntityMapper.toDomain(persistedUserEntity, domainUser.getPhones()))
                .thenReturn(domainUser);

        // act
        User result = userRepositoryAdapter.save(domainUser);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(domainUser);
        verify(entityManager, never()).persist(any(PhoneEntity.class));
    }

    @Test
    void existsByEmail_shouldDelegateToRepository() {
        // given
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // act
        boolean result = userRepositoryAdapter.existsByEmail(email);

        // then
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userEntityMapper, entityManager);
    }
}
