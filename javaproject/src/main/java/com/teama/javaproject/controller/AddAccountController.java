package com.teama.javaproject.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

import com.teama.javaproject.service.AddAccountService;

@RestController
@RequestMapping("/api/account")
public class AddAccountController {

    @Autowired
    private AddAccountService addAccountService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        boolean result = addAccountService.addAccount(
            request.get("username"),
            request.get("email"),
            request.get("password"),
            request.get("role")
        );

        if (result) {
            return ResponseEntity.ok("Account created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
    }
}
