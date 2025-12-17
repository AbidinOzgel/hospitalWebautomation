package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Role;
import com.abidin.hospital.entity.User;
import com.abidin.hospital.repository.RoleRepository;
import com.abidin.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // --------- KAYIT OL (REGISTER) ---------
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Rolleri hazırla (gönderilen isimlere göre)
        Set<Role> roles = new HashSet<>();
        if (request.roles() != null && !request.roles().isEmpty()) {
            for (String roleName : request.roles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            // Rol yoksa otomatik oluştur (kolaylık için)
                            Role newRole = new Role();
                            newRole.setName(roleName);
                            newRole.setDescription(roleName + " role");
                            return roleRepository.save(newRole);
                        });
                roles.add(role);
            }
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .active(true)
                .roles(roles)
                .build();

        user = userRepository.save(user);

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getActive(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }

    // --------- GİRİŞ (LOGIN) ---------
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.getActive()) {
            throw new RuntimeException("User is inactive");
        }

        boolean matches = passwordEncoder.matches(request.password(), user.getPassword());
        if (!matches) {
            throw new RuntimeException("Invalid username or password");
        }

        // Şimdilik token üretmiyoruz; sadece user bilgisi dönüyoruz.
        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getActive(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }

    // --------- DTO'lar ---------

    public record RegisterRequest(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            List<String> roles
    ) {}

    public record LoginRequest(
            String username,
            String password
    ) {}

    public record AuthResponse(
            Long id,
            String username,
            String fullName,
            String email,
            String phone,
            Boolean active,
            List<String> roles
    ) {}
}
