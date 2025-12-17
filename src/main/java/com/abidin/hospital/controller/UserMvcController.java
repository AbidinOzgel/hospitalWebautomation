package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Role;
import com.abidin.hospital.entity.User;
import com.abidin.hospital.repository.RoleRepository;
import com.abidin.hospital.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserMvcController extends BaseController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String list(Model model, HttpSession session) {
        setCommonModel(model, session, "users", "Kullanıcılar");
        List<User> users = userRepository.findAll();
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("allRoles", roles);
        model.addAttribute("contentTemplate", "users/list");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        setCommonModel(model, session, "users", "Yeni Kullanıcı");
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("contentTemplate", "users/form");
        return "layout";
    }

    @PostMapping
    public String create(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String fullName,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false, name = "roleNames") List<String> roleNames,
                         HttpSession session) {

        if (userRepository.findByUsername(username).isPresent()) {
            session.setAttribute("errorMessage", "Bu kullanıcı adı zaten mevcut.");
            return "redirect:/users";
        }

        Set<Role> roles = new HashSet<>();
        if (roleNames != null) {
            for (String r : roleNames) {
                Role role = roleRepository.findByName(r)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + r));
                roles.add(role);
            }
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .active(true)
                .roles(roles)
                .build();

        userRepository.save(user);
        session.setAttribute("flashMessage", "Kullanıcı oluşturuldu.");
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "users", "Kullanıcı Düzenle");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        model.addAttribute("contentTemplate", "users/form");
        return "layout";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String fullName,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String phone,
                         @RequestParam(required = false) String password,
                         @RequestParam(required = false, name = "active") Boolean active,
                         @RequestParam(required = false, name = "roleNames") List<String> roleNames,
                         HttpSession session) {

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setFullName(fullName);
        existing.setEmail(email);
        existing.setPhone(phone);
        existing.setActive(active != null ? active : existing.getActive());

        if (password != null && !password.isBlank()) {
            existing.setPassword(passwordEncoder.encode(password));
        }

        if (roleNames != null) {
            Set<Role> roles = new HashSet<>();
            for (String r : roleNames) {
                Role role = roleRepository.findByName(r)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + r));
                roles.add(role);
            }
            existing.setRoles(roles);
        }

        userRepository.save(existing);
        session.setAttribute("flashMessage", "Kullanıcı güncellendi.");
        return "redirect:/users";
    }
}
