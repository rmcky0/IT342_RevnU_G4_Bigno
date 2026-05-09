package com.revnu.backend.features.auth.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.revnu.backend.features.files.model.FileRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    @Column(nullable = false)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleType role;

    @Column(unique = true)
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "provider", length = 50)
    private String provider = "local";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_file_id", foreignKey = @ForeignKey(name = "fk_users_avatar_file"))
    private FileRecord avatarFile;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {

        private UUID id;
        private String email;
        private String passwordHash;
        private String fullname;
        private RoleType role;
        private String oauthId;
        private AccountStatus status = AccountStatus.ACTIVE;
        private String provider = "local";

        public UserBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public UserBuilder fullname(String fullname) {
            this.fullname = fullname;
            return this;
        }

        public UserBuilder role(RoleType role) {
            this.role = role;
            return this;
        }

        public UserBuilder oauthId(String oauthId) {
            this.oauthId = oauthId;
            return this;
        }

        public UserBuilder status(AccountStatus status) {
            this.status = status;
            return this;
        }

        public UserBuilder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.email = this.email;
            user.passwordHash = this.passwordHash;
            user.fullname = this.fullname;
            user.role = this.role;
            user.oauthId = this.oauthId;
            user.status = this.status;
            user.provider = this.provider;
            return user;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;

    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public FileRecord getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(FileRecord avatarFile) {
        this.avatarFile = avatarFile;
    }

}
