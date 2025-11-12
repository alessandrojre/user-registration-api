package com.user.infrastructure.persistence.jpa.repository.phone;

import com.user.infrastructure.persistence.jpa.model.PhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PhoneRepository extends JpaRepository<PhoneEntity, UUID> {
    
    @Query("select p from PhoneEntity p where p.user.id = :userId")
    List<PhoneEntity> findByUserId(@Param("userId") UUID userId);
}
