package com.teama.javaproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teama.javaproject.repository.UserRepository;
import com.teama.javaproject.entity.User;

@Service
public class AddAccountServiceImpl implements AddAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean addAccount(String username, String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        // パスワードをハッシュ化して保存
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);
        return true;
    }
}