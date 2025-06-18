package com.teama.javaproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teama.javaproject.repository.UserRepository;
import com.teama.javaproject.entity.User;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AddAccountServiceImpl implements AddAccountService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean addAccount(String username, String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setRole(role);

        userRepository.save(user);
        return true;
    }
}