package com.teama.javaproject.controller;

import com.teama.javaproject.service.LoginService;
import com.teama.javaproject.entity.User;
import com.teama.javaproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
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

        if (userOpt.isPresent() && loginService.login(email, password)) {
            session.setAttribute("loginUser", userOpt.get());
            return "redirect:/home";  // 🔁 成功したら/homeへ
        } else {
            model.addAttribute("error", "メールかパスワードが間違っています");
            return "login";  // 再び login.html を表示
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
