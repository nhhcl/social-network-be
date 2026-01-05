package com.social.network.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter
@Setter
public class TokenEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String type; // REFRESH / ACCESS

    @Column
    private Boolean expired;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    private boolean revoked = false;
}
