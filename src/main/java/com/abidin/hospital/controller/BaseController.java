package com.abidin.hospital.controller;

import com.abidin.hospital.config.security.CustomUserDetails;
import com.abidin.hospital.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

public abstract class BaseController {

    protected void setCommonModel(Model model, HttpSession session, String activePage, String pageTitle) {

        // Güvenlikten gelen kullanıcıyı modele koy
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null &&
                auth.isAuthenticated() &&
                !(auth instanceof AnonymousAuthenticationToken)) {

            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetails cud) {
                User currentUser = cud.getUser();
                model.addAttribute("currentUser", currentUser);

                String roleText = currentUser.getRoles().stream()
                        .map(r -> r.getName())
                        .findFirst()
                        .orElse("Kullanıcı");
                model.addAttribute("roleText", roleText);
            }
        }

        // Flash / hata mesajları
        Object flash = session.getAttribute("flashMessage");
        if (flash != null) {
            model.addAttribute("flashMessage", flash.toString());
            session.removeAttribute("flashMessage");
        }

        Object err = session.getAttribute("errorMessage");
        if (err != null) {
            model.addAttribute("errorMessage", err.toString());
            session.removeAttribute("errorMessage");
        }

        model.addAttribute("activePage", activePage);
        model.addAttribute("pageTitle", pageTitle);
    }
}
