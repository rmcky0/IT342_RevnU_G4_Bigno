package com.revnu.backend.features.settings.service;

import java.time.LocalTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.files.model.FileRecord;
import com.revnu.backend.features.files.repository.FileRecordRepository;
import com.revnu.backend.features.files.service.SupabaseStorageService;
import com.revnu.backend.features.notifications.service.NotificationService;
import com.revnu.backend.features.restaurants.model.AppSettings;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.settings.dto.ChangePasswordRequest;
import com.revnu.backend.features.settings.dto.RestaurantProfileRequest;
import com.revnu.backend.features.settings.dto.RestaurantProfileResponse;
import com.revnu.backend.features.settings.dto.RestaurantSetupRequest;
import com.revnu.backend.features.settings.dto.SystemSettingsDto;
import com.revnu.backend.features.settings.dto.UserProfileDto;

@Service
public class SettingsService {

    private final FileRecordRepository fileRecordRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SupabaseStorageService supabaseStorageService;
    private final NotificationService notificationService;

    public SettingsService(
            FileRecordRepository fileRecordRepository,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SupabaseStorageService supabaseStorageService,
            NotificationService notificationService
    ) {
        this.fileRecordRepository = fileRecordRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.supabaseStorageService = supabaseStorageService;
        this.notificationService = notificationService;
    }

    public UserProfileDto getProfile(String email) {
        User user = getAuthenticatedUser(email);
        return toUserDto(user);
    }

    @Transactional
    public UserProfileDto updateProfile(String email, UserProfileDto request) {
        User user = getAuthenticatedUser(email);
        user.setFullname(request.fullname());
        userRepository.save(user);
        return toUserDto(user);
    }

    @Transactional
    public UserProfileDto updateProfileAvatar(String email, MultipartFile file) {
        User user = getAuthenticatedUser(email);
        user.setAvatarFile(storeFile(file, "avatars"));
        userRepository.save(user);
        return toUserDto(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getAuthenticatedUser(email);

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new IllegalStateException("Password change is not available for Google-linked accounts.");
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public RestaurantProfileResponse setupRestaurant(String email, RestaurantSetupRequest request, MultipartFile logoFile) {
        User owner = getAuthenticatedUser(email);

        if (restaurantRepository.existsByOwner(owner)) {
            throw new IllegalStateException("Restaurant profile already exists for this user.");
        }

        Restaurant restaurant = Restaurant.builder()
                .owner(owner)
                .name(request.name())
                .physicalLocation(request.physicalLocation())
                .openingHrs(request.openingHrs())
                .closingHrs(request.closingHrs())
                .appSettings(new AppSettings())
                .build();

        if (logoFile != null && !logoFile.isEmpty()) {
            restaurant.setLogoFile(storeFile(logoFile, "logos"));
        }

        Restaurant saved = restaurantRepository.save(restaurant);
        notificationService.notifyAdminsRestaurantCreated(saved);
        return toResponse(saved);
    }

    public RestaurantProfileResponse getRestaurantProfile(String email) {
        return toResponse(getRestaurant(getAuthenticatedUser(email)));
    }

    @Transactional
    public RestaurantProfileResponse updateRestaurantProfile(String email, RestaurantProfileRequest request) {
        Restaurant restaurant = getRestaurant(getAuthenticatedUser(email));

        restaurant.setName(request.restaurantName());
        restaurant.setPhysicalLocation(request.physicalAddress());

        if (request.openingHours() != null && !request.openingHours().isEmpty()) {
            restaurant.setOpeningHrs(LocalTime.parse(request.openingHours()));
        }
        if (request.closingHours() != null && !request.closingHours().isEmpty()) {
            restaurant.setClosingHrs(LocalTime.parse(request.closingHours()));
        }

        return toResponse(restaurantRepository.save(restaurant));
    }

    public SystemSettingsDto getSystemSettings(String email) {
        AppSettings s = getRestaurant(getAuthenticatedUser(email)).getAppSettings();
        return new SystemSettingsDto(
                s.isEmailNotifications(), s.isPushNotifications(),
                s.isRequireReceiptPhoto(), s.isSoftLockRecords()
        );
    }

    @Transactional
    public SystemSettingsDto updateSystemSettings(String email, SystemSettingsDto request) {
        Restaurant restaurant = getRestaurant(getAuthenticatedUser(email));
        AppSettings s = restaurant.getAppSettings();

        s.setEmailNotifications(request.emailNotifications());
        s.setPushNotifications(request.pushNotifications());
        s.setRequireReceiptPhoto(request.requireReceiptPhoto());
        s.setSoftLockRecords(request.softLockRecords());

        restaurantRepository.save(restaurant);
        return request;
    }

    @Transactional
    public void updateRestaurantLogo(String email, MultipartFile file) {
        Restaurant restaurant = getRestaurant(getAuthenticatedUser(email));
        restaurant.setLogoFile(storeFile(file, "logos"));
        restaurantRepository.save(restaurant);
    }

    private User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private Restaurant getRestaurant(User owner) {
        return restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant profile not found. Please complete setup first."));
    }

    private String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        return time.toString().substring(0, 5);
    }

    private UserProfileDto toUserDto(User u) {
        return new UserProfileDto(
                u.getId(),
                u.getFullname(),
                u.getEmail(),
                u.getProvider(),
                u.getAvatarFile() != null ? u.getAvatarFile().getId() : null
        );
    }

    private RestaurantProfileResponse toResponse(Restaurant r) {
        return new RestaurantProfileResponse(
                r.getId(),
                r.getName(),
                r.getPhysicalLocation(),
                formatTime(r.getOpeningHrs()),
                formatTime(r.getClosingHrs()),
                r.getLogoFile() != null ? r.getLogoFile().getId() : null
        );
    }

    private FileRecord storeFile(MultipartFile file, String folder) {
        String publicUrl = supabaseStorageService.uploadFile(file, folder);

        FileRecord record = new FileRecord();
        record.setFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload");
        record.setFilepath(publicUrl);
        record.setFiletype(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        return fileRecordRepository.save(record);
    }
}
