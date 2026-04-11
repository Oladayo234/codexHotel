package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.Admin;
import com.semicolon.codexHotel.data.models.enums.Role;
import com.semicolon.codexHotel.data.repositories.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;

    @Override
    public void run(String... args) {
        if (adminRepository.findByEmail("admin@codexhotel.com").isEmpty()) {
            Admin admin = new Admin();
            admin.setName("Madam Bolu");
            admin.setAdminReferenceNumber(AdminReferenceGenerator.generateAdminReference());
            admin.setEmail("admin@codexhotel.com");
            admin.setPassword("Admin@1234");
            admin.setRole(Role.ADMIN);
            adminRepository.save(admin);
            log.info("Admin seeded successfully");
        }
    }
}