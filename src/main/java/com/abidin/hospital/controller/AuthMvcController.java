package com.abidin.hospital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthMvcController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Kullanıcı adı veya şifre hatalı.");
        }
        if (logout != null) {
            model.addAttribute("flashMessage", "Çıkış yapıldı.");
        }
        return "login";
    }
}
