package com.teama.javaproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.teama.javaproject.entity.User;
import com.teama.javaproject.repository.UserRepository;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean login(User user, String password) {
        // ハッシュ化されたパスワードと入力されたパスワードを比較
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
}