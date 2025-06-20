package com.teama.javaproject.controller;

import com.teama.javaproject.entity.User;
import com.teama.javaproject.repository.UserRepository;
import com.teama.javaproject.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && loginService.login(userOpt.get(), password)) {
            session.setAttribute("loginUser", userOpt.get());
            return "redirect:/home";
        } else {
            model.addAttribute("error", "メールかパスワードが間違っています");
            return "login";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
