package com.teama.javaproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import com.teama.javaproject.service.LoginService;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        boolean result = loginService.login(email, password);
        if (result) {
            return new ResponseEntity<>("ログイン成功", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ログイン失敗", HttpStatus.UNAUTHORIZED);
        }
    }
}