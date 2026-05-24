package com.revnu.backend.features.notifications.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.notifications.dto.HolidayNotificationRequest;
import com.revnu.backend.features.notifications.dto.NotificationResponse;
import com.revnu.backend.features.notifications.dto.SystemNotificationRequest;
import com.revnu.backend.features.notifications.model.Notification;
import com.revnu.backend.features.notifications.model.NotificationType;
import com.revnu.backend.features.notifications.repository.NotificationRepository;
import com.revnu.backend.features.reporting.model.DailySummary;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;

@Service
public class NotificationService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getRecent(String email, int limit) {
        User user = getUser(email);
        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(user, PageRequest.of(0, limit))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        User user = getUser(email);
        return notificationRepository.countByRecipientAndReadAtIsNull(user);
    }

    @Transactional
    public NotificationResponse markRead(String email, UUID id) {
        User user = getUser(email);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found."));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Notification does not belong to user.");
        }

        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return toResponse(notification);
    }

    @Transactional
    public int markAllRead(String email) {
        User user = getUser(email);
        List<Notification> unread = notificationRepository.findByRecipientAndReadAtIsNull(user);

        if (unread.isEmpty()) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        unread.forEach(n -> n.setReadAt(now));
        notificationRepository.saveAll(unread);
        return unread.size();
    }

    @Transactional
    public NotificationResponse createSystemNotification(String email, SystemNotificationRequest request) {
        User user = getUser(email);
        Restaurant restaurant = getRestaurant(user);
        return createNotification(user, restaurant, NotificationType.SYSTEM, request.title(), request.message(), null);
    }

    @Transactional
    public NotificationResponse createHolidayNotification(String email, HolidayNotificationRequest request) {
        User user = getUser(email);
        Restaurant restaurant = getRestaurant(user);

        LocalDate date = request.date();
        String eventKey = date == null ? null : "HOLIDAY:" + date;
        String safeName = request.name() == null ? "" : request.name().trim();
        String title = safeName.isBlank() ? "Holiday Alert" : safeName;
        String message;
        if (date == null) {
            message = safeName.isBlank()
                    ? "Today is a holiday."
                    : "Today is " + safeName + ".";
        } else {
            String dateLabel = date.format(DATE_FORMAT);
            message = safeName.isBlank()
                    ? "Today, " + dateLabel + ", is a holiday."
                    : "Today, " + dateLabel + ", is " + safeName + ".";
        }

        return createNotification(user, restaurant, NotificationType.HOLIDAY, title, message, eventKey);
    }

    @Transactional
    public NotificationResponse createEodCloseNotification(User user, Restaurant restaurant, DailySummary summary) {
        String dateLabel = summary.getReportDate().format(DATE_FORMAT);
        String title = "EOD Locked";
        String message = "Records for " + dateLabel + " have been locked.";
        String eventKey = "EOD:CLOSE:" + summary.getReportDate();
        return createNotification(user, restaurant, NotificationType.EOD, title, message, eventKey);
    }

    @Transactional
    public NotificationResponse createEodEmailSentNotification(User user, Restaurant restaurant, DailySummary summary) {
        String dateLabel = summary.getReportDate().format(DATE_FORMAT);
        String title = "EOD Email Sent";
        String message = "EOD report email sent for " + dateLabel + ".";
        String eventKey = "EOD:EMAIL:" + summary.getReportDate();
        return createNotification(user, restaurant, NotificationType.EOD, title, message, eventKey);
    }

    @Transactional
    public void notifyAdminsUserRegistered(User user) {
        String title = "New Restaurateur Registered";
        String message = user.getFullname() + " registered with " + user.getEmail() + ".";
        String eventKey = "ADMIN:USER_REGISTER:" + user.getId();
        notifyAdmins(title, message, eventKey, null);
    }

    @Transactional
    public void notifyAdminsRestaurantCreated(Restaurant restaurant) {
        String title = "Restaurant Created";
        String message = restaurant.getName() + " was created by " + restaurant.getOwner().getEmail() + ".";
        String eventKey = "ADMIN:RESTAURANT_CREATE:" + restaurant.getId();
        notifyAdmins(title, message, eventKey, restaurant);
    }

    @Transactional
    public void notifyAdminsUserSuspended(User user) {
        String title = "Restaurateur Suspended";
        String message = user.getFullname() + " was suspended (" + user.getEmail() + ").";
        String eventKey = "ADMIN:USER_SUSPEND:" + user.getId();
        notifyAdmins(title, message, eventKey, null);
    }

    @Transactional
    public void notifyAdminsUserPromoted(User user) {
        String title = "Restaurateur Promoted to Admin";
        String message = user.getFullname() + " was promoted to admin (" + user.getEmail() + ").";
        String eventKey = "ADMIN:USER_PROMOTE:" + user.getId();
        notifyAdmins(title, message, eventKey, null);
    }

    public SseEmitter subscribe(String email) {
        User user = getUser(email);
        SseEmitter emitter = new SseEmitter(0L);

        emitters.put(user.getId(), emitter);
        emitter.onCompletion(() -> emitters.remove(user.getId()));
        emitter.onTimeout(() -> emitters.remove(user.getId()));
        emitter.onError(e -> emitters.remove(user.getId()));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (Exception ignored) {
            emitters.remove(user.getId());
        }

        return emitter;
    }

    private NotificationResponse createNotification(
            User user,
            Restaurant restaurant,
            NotificationType type,
            String title,
            String message,
            String eventKey
    ) {
        if (eventKey != null && notificationRepository.existsByRecipientAndEventKey(user, eventKey)) {
            Notification existing = notificationRepository.findByRecipientAndEventKey(user, eventKey)
                    .orElse(null);
            if (existing != null) {
                return toResponse(existing);
            }
        }

        Notification notification = new Notification();
        notification.setRecipient(user);
        notification.setRestaurant(restaurant);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setEventKey(eventKey);

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = toResponse(saved);
        publish(user, response);
        return response;
    }

    private void notifyAdmins(String title, String message, String eventKey, Restaurant restaurant) {
        List<User> admins = userRepository.findByRole(RoleType.ADMIN);
        for (User admin : admins) {
            createNotification(admin, restaurant, NotificationType.ADMIN, title, message, eventKey);
        }
    }

    private void publish(User user, NotificationResponse response) {
        SseEmitter emitter = emitters.get(user.getId());
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event().name("notification").data(response));
        } catch (Exception e) {
            emitters.remove(user.getId());
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType().name(),
                notification.getReadAt() != null,
                notification.getCreatedAt()
        );
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private Restaurant getRestaurant(User user) {
        return restaurantRepository.findByOwner(user)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found."));
    }
}
