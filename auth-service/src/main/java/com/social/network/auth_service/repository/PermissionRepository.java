package com.social.network.auth_service.repository;

import com.social.network.auth_service.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {}
