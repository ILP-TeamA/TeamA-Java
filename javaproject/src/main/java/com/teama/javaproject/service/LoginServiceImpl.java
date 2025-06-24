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
        String storedPassword = user.getPasswordHash();
        
        // ハッシュ化されたパスワードかどうかをチェック
        if (isHashedPassword(storedPassword)) {
            // ハッシュ化されている場合：通常の比較
            return passwordEncoder.matches(password, storedPassword);
        } else {
            // 平文の場合：平文比較 + 自動ハッシュ化
            if (password.equals(storedPassword)) {
                // ログイン成功時に自動的にハッシュ化して更新
                user.setPasswordHash(passwordEncoder.encode(password));
                userRepository.save(user);
                return true;
            }
            return false;
        }
    }
    
    /**
     * パスワードがハッシュ化されているかどうかを判定
     * BCryptハッシュは$2a$、$2b$、$2y$で始まり、60文字
     */
    private boolean isHashedPassword(String password) {
        return password != null && 
               password.length() == 60 && 
               (password.startsWith("$2a$") || 
                password.startsWith("$2b$") || 
                password.startsWith("$2y$"));
    }
}