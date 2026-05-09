package com.revnu.backend.features.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByOauthId(String oauthId);

    Optional<User> findByOauthId(String oauthId);

    Page<User> findByRole(RoleType role, Pageable pageable);

    List<User> findByRole(RoleType role);

    long countByRole(RoleType role);

    long countByRoleAndCreatedAtAfter(RoleType role, LocalDateTime after);

    long countByRoleAndStatus(RoleType role, AccountStatus status);
}
