package com.abidin.hospital.config;

import com.abidin.hospital.entity.Role;
import com.abidin.hospital.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        // Sistemde hiç rol yoksa veya bazıları eksikse tamamla
        List<RoleSeed> defaultRoles = Arrays.asList(
                new RoleSeed("ADMIN", "Sistem yöneticisi"),
                new RoleSeed("DOCTOR", "Doktor"),
                new RoleSeed("NURSE", "Hemşire"),
                new RoleSeed("RECEPTION", "Kayıt / danışma"),
                new RoleSeed("LAB_TECH", "Laboratuvar personeli")
        );

        for (RoleSeed seed : defaultRoles) {
            roleRepository.findByName(seed.name)
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName(seed.name);
                        r.setDescription(seed.description);
                        return roleRepository.save(r);
                    });
        }
    }

    private record RoleSeed(String name, String description) {}
}
