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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getUsers(@RequestParam(value = "role", required = false) String roleName) {
        if (roleName != null && !roleName.isBlank()) {
            return userRepository.findByRoles_Name(roleName);
        }
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Set<Role> roles = new HashSet<>();
        if (request.roles() != null) {
            for (String roleName : request.roles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password())) // HASHLİ
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .active(request.active() == null ? true : request.active())
                .roles(roles)
                .build();

        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.fullName() != null) existing.setFullName(request.fullName());
        if (request.email() != null) existing.setEmail(request.email());
        if (request.phone() != null) existing.setPhone(request.phone());
        if (request.active() != null) existing.setActive(request.active());

        // Şifre güncelleme istenmişse:
        if (request.password() != null && !request.password().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.roles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.roles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
            existing.setRoles(roles);
        }

        return userRepository.save(existing);
    }

    // ------------ DTO'lar ------------

    public record CreateUserRequest(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            Boolean active,
            List<String> roles
    ) {}

    public record UpdateUserRequest(
            String fullName,
            String email,
            String phone,
            Boolean active,
            String password,
            List<String> roles
    ) {}
}
