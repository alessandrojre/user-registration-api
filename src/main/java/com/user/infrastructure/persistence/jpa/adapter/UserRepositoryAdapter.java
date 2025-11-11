package com.user.infrastructure.persistence.jpa.adapter;

import com.user.domain.user.User;
import com.user.domain.user.port.UserRepositoryPort;
import com.user.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import com.user.infrastructure.persistence.jpa.model.PhoneEntity;
import com.user.infrastructure.persistence.jpa.model.UserEntity;
import com.user.infrastructure.persistence.jpa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public UserRepositoryAdapter(UserRepository userRepository,
                                 UserEntityMapper userEntityMapper) {
        this.userRepository = userRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);

        UserEntity persistedUserEntity = userRepository.save(userEntity);

        List<PhoneEntity> phoneEntities = userEntityMapper.toPhoneEntities(persistedUserEntity, user.getPhones());
        phoneEntities.forEach(entityManager::persist);

        return userEntityMapper.toDomain(persistedUserEntity, user.getPhones());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}