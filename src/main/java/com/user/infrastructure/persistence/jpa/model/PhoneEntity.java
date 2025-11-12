package com.user.infrastructure.persistence.jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "phones", indexes = {
        @Index(name = "idx_phones_user_id", columnList = "user_id")
})
@Getter
@Setter
public class PhoneEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;
    private String cityCode;
    private String countryCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
