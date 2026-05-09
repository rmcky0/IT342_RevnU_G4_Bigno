package com.revnu.backend.features.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revnu.backend.features.admin.dto.AdminRestaurantResponse;
import com.revnu.backend.features.admin.dto.AdminStatsResponse;
import com.revnu.backend.features.admin.dto.AdminUserResponse;
import com.revnu.backend.features.admin.dto.UpdateUserRoleRequest;
import com.revnu.backend.features.admin.dto.UpdateUserStatusRequest;
import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.notifications.service.NotificationService;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final NotificationService notificationService;

    public AdminService(UserRepository userRepository,
            RestaurantRepository restaurantRepository,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getPlatformStats() {
        long totalTenants = userRepository.countByRole(RoleType.TENANT);
        long totalRestaurants = restaurantRepository.count();

        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long newTenantsThisMonth = userRepository.countByRoleAndCreatedAtAfter(
                RoleType.TENANT, startOfMonth);

        long activeTenants = userRepository.countByRoleAndStatus(RoleType.TENANT, AccountStatus.ACTIVE);
        long suspendedTenants = userRepository.countByRoleAndStatus(RoleType.TENANT, AccountStatus.SUSPENDED);

        return new AdminStatsResponse(
                totalTenants, totalRestaurants, newTenantsThisMonth,
                activeTenants, suspendedTenants
        );
    }

    // ── User Management ───────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> usersPage = userRepository.findAll(pageable);

        List<User> users = usersPage.getContent();
        List<Restaurant> restaurants = restaurantRepository.findByOwnerIn(users);
        Map<UUID, Restaurant> restaurantMap = restaurants.stream()
                .collect(Collectors.toMap(r -> r.getOwner().getId(), r -> r));

        return usersPage.map(user -> toUserResponse(user, restaurantMap.get(user.getId())));
    }

    @Transactional
    public AdminUserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        User user = getUser(userId);
        if (user.getRole() == RoleType.ADMIN) {
            throw new IllegalStateException("Cannot suspend an admin account.");
        }

        boolean suspendAction = request.status() == AccountStatus.SUSPENDED;
        user.setStatus(request.status());
        userRepository.save(user);
        Restaurant r = restaurantRepository.findByOwner(user).orElse(null);
        if (suspendAction) {
            notificationService.notifyAdminsUserSuspended(user);
        }
        return toUserResponse(user, r);
    }

    @Transactional
    public AdminUserResponse updateUserRole(UUID userId, UpdateUserRoleRequest request) {
        User user = getUser(userId);
        boolean promotedToAdmin = user.getRole() != RoleType.ADMIN && request.role() == RoleType.ADMIN;
        user.setRole(request.role());
        userRepository.save(user);
        Restaurant r = restaurantRepository.findByOwner(user).orElse(null);
        if (promotedToAdmin) {
            notificationService.notifyAdminsUserPromoted(user);
        }
        return toUserResponse(user, r);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = getUser(userId);
        if (user.getRole() == RoleType.ADMIN) {
            throw new IllegalStateException("Cannot delete an admin account.");
        }
        restaurantRepository.findByOwner(user).ifPresent(restaurantRepository::delete);
        userRepository.delete(user);
    }

    // ── Restaurant Directory ──────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<AdminRestaurantResponse> getAllRestaurants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return restaurantRepository.findAll(pageable).map(r -> new AdminRestaurantResponse(
                r.getId(),
                r.getName(),
                r.getPhysicalLocation(),
                r.getCreatedAt(),
                r.getOwner().getId(),
                r.getOwner().getFullname(),
                r.getOwner().getEmail(),
                r.getOwner().getStatus(),
                r.getLogoFile() != null ? r.getLogoFile().getId() : null,
                r.getOwner().getAvatarFile() != null ? r.getOwner().getAvatarFile().getId() : null
        ));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private AdminUserResponse toUserResponse(User user, Restaurant r) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullname(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                r != null ? r.getId() : null,
                r != null ? r.getName() : null,
                user.getAvatarFile() != null ? user.getAvatarFile().getId() : null
        );
    }
}
