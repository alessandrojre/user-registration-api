package com.user.infrastructure.persistence.jpa.adapter;

import com.user.domain.user.User;
import com.user.domain.user.port.UserRepositoryPort;
import com.user.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import com.user.infrastructure.persistence.jpa.model.PhoneEntity;
import com.user.infrastructure.persistence.jpa.model.UserEntity;
import com.user.infrastructure.persistence.jpa.repository.phone.PhoneRepository;
import com.user.infrastructure.persistence.jpa.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final PhoneRepository phoneRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryAdapter(UserRepository userRepository,
                                 PhoneRepository phoneRepository,
                                 UserEntityMapper mapper) {
        this.userRepository = userRepository;
        this.phoneRepository = phoneRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity persisted = userRepository.save(entity);

        List<PhoneEntity> phoneEntities = user.getPhones().stream().map(p -> {
            PhoneEntity pe = new PhoneEntity();
            pe.setNumber(p.getNumber());
            pe.setCityCode(p.getCityCode());
            pe.setCountryCode(p.getCountryCode());
            pe.setUser(persisted);
            return pe;
        }).toList();
        phoneRepository.saveAll(phoneEntities);

        List<PhoneEntity> reloaded = phoneRepository.findByUserId(persisted.getId());
        return mapper.toDomain(persisted, reloaded);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userEntity -> {
            List<PhoneEntity> phones = phoneRepository.findByUserId(userEntity.getId());
            return mapper.toDomain(userEntity, phones);
        });
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
