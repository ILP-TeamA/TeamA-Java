package com.teama.javaproject.controller;

import com.teama.javaproject.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");

        if (user == null) {
            return "redirect:/login"; // ログインしてなければログイン画面へ
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole());
        return "home";
    }
}
