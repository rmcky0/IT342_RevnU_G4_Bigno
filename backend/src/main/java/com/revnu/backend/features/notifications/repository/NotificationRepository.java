package com.revnu.backend.features.notifications.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.notifications.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    List<Notification> findByRecipientAndReadAtIsNull(User recipient);

    long countByRecipientAndReadAtIsNull(User recipient);

    boolean existsByRecipientAndEventKey(User recipient, String eventKey);

    Optional<Notification> findByRecipientAndEventKey(User recipient, String eventKey);
}
