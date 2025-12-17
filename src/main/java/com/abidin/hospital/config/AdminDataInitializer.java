package com.abidin.hospital.config;

import com.abidin.hospital.entity.Role;
import com.abidin.hospital.entity.User;
import com.abidin.hospital.repository.RoleRepository;
import com.abidin.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // ADMIN rolü hazır mı?
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ADMIN");
                    r.setDescription("Sistem yöneticisi");
                    return roleRepository.save(r);
                });

        // === 1. ADMIN (varsayılan) ===
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin1 = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Sistem Yöneticisi 1")
                    .email("admin1@hospital.local")
                    .active(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin1);
        }

        // === 2. ADMIN (senin istediğin) ===
        if (userRepository.findByUsername("admin2").isEmpty()) {
            User admin2 = User.builder()
                    .username("admin2")
                    .password(passwordEncoder.encode("admin456"))  // şifreyi istediğin gibi belirle
                    .fullName("Sistem Yöneticisi 2")
                    .email("admin2@hospital.local")
                    .active(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin2);
        }
    }
}