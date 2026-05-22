package com.revnu.backend.features.auth.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.reporting.service.EmailService;

@Service
public class PasswordResetService {

    private static final long OTP_EXPIRY_SECONDS = 600; // 10 minutes
    private static final SecureRandom RANDOM = new SecureRandom();

    private record OtpEntry(String otp, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    private final ConcurrentHashMap<String, OtpEntry> store = new ConcurrentHashMap<>();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void requestOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found with that email."));

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "This account uses Google sign-in. Password reset is not available.");
        }

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        store.put(email, new OtpEntry(otp, Instant.now().plusSeconds(OTP_EXPIRY_SECONDS)));
        emailService.sendOtpEmail(email, user.getFullname(), otp);
    }

    public void verifyOtp(String email, String otp) {
        OtpEntry entry = store.get(email);

        if (entry == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No OTP was requested for this email.");
        }
        if (entry.isExpired()) {
            store.remove(email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new one.");
        }
        if (!entry.otp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect OTP.");
        }
    }

    public void resetPassword(String email, String otp, String newPassword) {
        OtpEntry entry = store.get(email);

        if (entry == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No OTP was requested for this email.");
        }
        if (entry.isExpired()) {
            store.remove(email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new one.");
        }
        if (!entry.otp().equals(otp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect OTP.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        store.remove(email);
    }
}
