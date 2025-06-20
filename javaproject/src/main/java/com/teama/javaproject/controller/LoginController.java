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
            return "redirect:/home"; // ログイン成功 → ホーム画面へ
        } else {
            model.addAttribute("error", "メールかパスワードが間違っています");
            return "login"; // ログイン画面に戻る
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
