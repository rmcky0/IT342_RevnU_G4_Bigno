package com.revnu.backend.features.admin;

import com.revnu.backend.features.admin.dto.AdminUserResponse;
import com.revnu.backend.features.admin.dto.UpdateUserStatusRequest;
import com.revnu.backend.features.admin.service.AdminService;
import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.notifications.service.NotificationService;
import com.revnu.backend.features.reporting.service.EmailService;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Unit Tests")
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks
    private AdminService adminService;

    private User adminUser;
    private User restaurateurUser;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@revnu.com")
                .fullname("Super Admin")
                .role(RoleType.ADMIN)
                .status(AccountStatus.ACTIVE)
                .provider("local")
                .build();

        restaurateurUser = User.builder()
                .id(UUID.randomUUID())
                .email("owner@test.com")
                .fullname("Restaurant Owner")
                .role(RoleType.RESTAURATEUR)
                .status(AccountStatus.ACTIVE)
                .provider("local")
                .build();
    }

    // ── Business Rule 1.2: ADMIN accounts cannot be suspended ────────────────────
    @Test
    @DisplayName("Rule 1.2 - updateUserStatus() throws when trying to suspend an ADMIN account")
    void updateUserStatus_suspendAdmin_throwsIllegalState() {
        UUID adminId = adminUser.getId();
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));

        UpdateUserStatusRequest req = new UpdateUserStatusRequest(AccountStatus.SUSPENDED);

        assertThatThrownBy(() -> adminService.updateUserStatus(adminId, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot suspend an admin account");
    }

    // ── Business Rule 1.2: ADMIN accounts cannot be deleted ──────────────────────
    @Test
    @DisplayName("Rule 1.2 - deleteUser() throws when trying to delete an ADMIN account")
    void deleteUser_adminUser_throwsIllegalState() {
        UUID adminId = adminUser.getId();
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> adminService.deleteUser(adminId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete an admin account");
    }

    // ── Business Rule 1.5: Suspending a RESTAURATEUR blocks their account ────────
    @Test
    @DisplayName("Rule 1.5 - suspendUser() sets SUSPENDED status and notifies admins")
    void suspendUser_restaurateur_setsStatusAndSendsEmail() {
        UUID userId = restaurateurUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(restaurateurUser));
        when(restaurantRepository.findByOwner(restaurateurUser)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(restaurateurUser);

        AdminUserResponse response = adminService.suspendUser(userId);

        assertThat(restaurateurUser.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
        verify(emailService).sendSuspensionEmail(anyString(), anyString());
        verify(notificationService).notifyAdminsUserSuspended(restaurateurUser);
    }

    // ── Business Rule 1.5: Reactivating restores ACTIVE status ──────────────────
    @Test
    @DisplayName("Rule 1.5 - reactivateUser() sets ACTIVE status and sends email")
    void reactivateUser_success_setsActiveStatusAndSendsEmail() {
        restaurateurUser.setStatus(AccountStatus.SUSPENDED);
        UUID userId = restaurateurUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(restaurateurUser));
        when(restaurantRepository.findByOwner(restaurateurUser)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(restaurateurUser);

        adminService.reactivateUser(userId);

        assertThat(restaurateurUser.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        verify(emailService).sendReactivationEmail(anyString(), anyString());
    }

    // ── Business Rule 1.2: updateUserStatus() on RESTAURATEUR succeeds ───────────
    @Test
    @DisplayName("Rule 1.2 - updateUserStatus() can suspend a RESTAURATEUR account")
    void updateUserStatus_restaurateur_succeeds() {
        UUID userId = restaurateurUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(restaurateurUser));
        when(restaurantRepository.findByOwner(restaurateurUser)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(restaurateurUser);

        UpdateUserStatusRequest req = new UpdateUserStatusRequest(AccountStatus.SUSPENDED);
        adminService.updateUserStatus(userId, req);

        assertThat(restaurateurUser.getStatus()).isEqualTo(AccountStatus.SUSPENDED);
        verify(emailService).sendSuspensionEmail(anyString(), anyString());
    }

    // ── Business Rule 1.2: deleting RESTAURATEUR also deletes their restaurant ───
    @Test
    @DisplayName("Rule 1.2 - deleteUser() deletes RESTAURATEUR and their restaurant")
    void deleteUser_restaurateur_deletesUserAndRestaurant() {
        UUID userId = restaurateurUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(restaurateurUser));
        when(restaurantRepository.findByOwner(restaurateurUser)).thenReturn(Optional.empty());

        adminService.deleteUser(userId);

        verify(userRepository).delete(restaurateurUser);
    }

    // ── Business Rule: Non-existent user throws exception ───────────────────────
    @Test
    @DisplayName("General - operations on non-existent user throw IllegalArgumentException")
    void updateUserStatus_nonExistentUser_throwsException() {
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.suspendUser(unknownId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }
}
