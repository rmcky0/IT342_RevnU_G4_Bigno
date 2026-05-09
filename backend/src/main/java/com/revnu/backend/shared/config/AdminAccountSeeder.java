package com.revnu.backend.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;

@Component
public class AdminAccountSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email}")
    private String adminEmail;

    @Value("${app.default-admin.password}")
    private String adminPassword;

    public AdminAccountSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            User adminUser = User.builder()
                    .email(adminEmail)
                    .fullname("System Administrator")
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(RoleType.ADMIN)
                    .provider("local")
                    .status(AccountStatus.ACTIVE)
                    .build();

            userRepository.save(adminUser);

            System.out.println("✅ Pre-made Admin account created successfully!");
        } else {
            System.out.println("ℹ️ Admin account already exists. Skipping seeding.");
        }
    }
}
