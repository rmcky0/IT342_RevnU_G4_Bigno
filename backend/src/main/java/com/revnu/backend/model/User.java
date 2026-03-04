package com.revnu.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;
    private String fullName;
    private LocalDate birthday;
    private String phoneNumber;

    // Emergency Contacts
    private String emergencyContactPerson;
    private String emergencyContactNumber;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private UserStatus status; // PENDING, ACTIVE, INACTIVE

    private String oauthId;
}