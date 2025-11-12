package com.user.infrastructure.persistence.jpa.adapter;

import com.user.domain.user.Phone;
import com.user.domain.user.User;
import com.user.domain.user.port.UserRepositoryPort;
import com.user.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import com.user.infrastructure.persistence.jpa.model.PhoneEntity;
import com.user.infrastructure.persistence.jpa.model.UserEntity;
import com.user.infrastructure.persistence.jpa.repository.phone.PhoneRepository;
import com.user.infrastructure.persistence.jpa.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PhoneRepository phoneRepository;
    @Mock
    private UserEntityMapper userEntityMapper;

    private UserRepositoryPort userRepositoryAdapter;

    @BeforeEach
    void setUp() {
        userRepositoryAdapter = new UserRepositoryAdapter(userRepository, phoneRepository, userEntityMapper);
    }


    @Test
    void save_shouldPersistUserAndPhonesAndReturnDomain() {
        // given
        UUID userId = UUID.randomUUID();
        User domainUser = buildDomainUser(userId);
        UserEntity userEntity = buildUserEntity(userId);
        UserEntity persistedUserEntity = buildUserEntity(userId);
        List<PhoneEntity> phoneEntities = List.of(buildPhoneEntity(persistedUserEntity));
        User expectedDomainUser = buildDomainUser(userId);

        when(userEntityMapper.toEntity(domainUser)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(persistedUserEntity);
        when(phoneRepository.findByUserId(persistedUserEntity.getId())).thenReturn(phoneEntities);
        when(userEntityMapper.toDomain(persistedUserEntity, phoneEntities)).thenReturn(expectedDomainUser);

        // act
        User result = userRepositoryAdapter.save(domainUser);

        // assert
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedDomainUser);

        InOrder inOrder = inOrder(userEntityMapper, userRepository, phoneRepository);
        inOrder.verify(userEntityMapper).toEntity(domainUser);
        inOrder.verify(userRepository).save(userEntity);
        inOrder.verify(phoneRepository).saveAll(anyList());
        inOrder.verify(phoneRepository).findByUserId(persistedUserEntity.getId());
        inOrder.verify(userEntityMapper).toDomain(persistedUserEntity, phoneEntities);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void findByEmail_shouldReturnMappedDomainUser() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = buildUserEntity(userId);
        List<PhoneEntity> phoneEntities = List.of(buildPhoneEntity(userEntity));
        User expectedDomainUser = buildDomainUser(userId);

        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));
        when(phoneRepository.findByUserId(userEntity.getId())).thenReturn(phoneEntities);
        when(userEntityMapper.toDomain(userEntity, phoneEntities)).thenReturn(expectedDomainUser);

        Optional<User> result = userRepositoryAdapter.findByEmail(userEntity.getEmail());

        assertThat(result).isPresent();
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(expectedDomainUser);

        InOrder inOrder = inOrder(userRepository, phoneRepository, userEntityMapper);
        inOrder.verify(userRepository).findByEmail(userEntity.getEmail());
        inOrder.verify(phoneRepository).findByUserId(userEntity.getId());
        inOrder.verify(userEntityMapper).toDomain(userEntity, phoneEntities);
    }

    @Test
    void existsByEmail_shouldDelegateToRepository() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userRepositoryAdapter.existsByEmail(email);

        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userEntityMapper, phoneRepository);
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
}
