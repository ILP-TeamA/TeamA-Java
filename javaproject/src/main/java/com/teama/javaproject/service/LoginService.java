package com.teama.javaproject.service;

import com.teama.javaproject.entity.User;

public interface LoginService {
    boolean login(User user, String password);
}

