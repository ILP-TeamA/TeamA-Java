package com.teama.javaproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.teama.javaproject.entity.User;
import com.teama.javaproject.repository.UserRepository;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean login(User user, String password) {
        // 直接比较明文密码
        return password.equals(user.getPasswordHash());
    }
}
