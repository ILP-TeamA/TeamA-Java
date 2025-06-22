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

        // 従業員の場合は直接販売実績入力画面へリダイレクト
        if ("employee".equals(user.getRole())) {
            return "redirect:/sales";
        }

        // 管理者の場合はホーム画面を表示
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole());
        return "home"; // home.html を表示
    }

    /**
     * アカウント管理画面へのリダイレクト
     */
    @GetMapping("/addaccount")
    public String redirectToAccountManagement(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");

        if (user == null) {
            return "redirect:/login";
        }

        // 管理者のみアクセス可能
        if (!"admin".equals(user.getRole())) {
            return "redirect:/home";
        }

        return "redirect:/account/management";
    }
}