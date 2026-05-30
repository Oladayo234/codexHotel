package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.Admin;
import com.semicolon.codexHotel.data.models.enums.Role;
import com.semicolon.codexHotel.data.repositories.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Saving admin to MongoDB...");
        adminRepository.deleteAll();
        Admin admin = new Admin();
        admin.setName("Madam Bolu");
        admin.setAdminReferenceNumber(AdminReferenceGenerator.generateAdminReference());
        admin.setEmail("admin@codexhotel.com");
        admin.setPassword(passwordEncoder.encode("Admin@1234"));
        admin.setRole(Role.ADMIN);
        Admin saved = adminRepository.save(admin);
        log.info("Saved admin with id: {}", saved.getId());
        log.info("Admin seeded successfully");
    }
}