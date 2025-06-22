package com.teama.javaproject.service;

import com.teama.javaproject.entity.User;
import com.teama.javaproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountManagementService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * アカウント作成
     */
    public boolean createAccount(String username, String email, String password, String role) {
        // メールアドレスの重複チェック
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }

        // 新しいユーザー作成
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);
        return true;
    }

    /**
     * メールアドレスでユーザー検索
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * ユーザーIDでユーザー検索
     */
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * 全ユーザー取得
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * ユーザーフィールド更新
     */
    public boolean updateUserField(Long userId, String fieldType, String newValue, String confirmValue) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        switch (fieldType) {
            case "username":
                if (newValue == null || newValue.trim().isEmpty()) {
                    return false;
                }
                user.setUsername(newValue.trim());
                break;
                
            case "email":
                if (newValue == null || newValue.trim().isEmpty()) {
                    return false;
                }
                
                // 新しいメールアドレスの重複チェック（自分以外）
                Optional<User> existingUser = userRepository.findByEmail(newValue.trim());
                if (existingUser.isPresent() && !existingUser.get().getUserId().equals(userId)) {
                    return false;
                }
                
                user.setEmail(newValue.trim());
                break;
                
            case "password":
                if (newValue == null || newValue.length() < 6) {
                    return false;
                }
                
                // パスワード確認チェック
                if (confirmValue == null || !newValue.equals(confirmValue)) {
                    return false;
                }
                
                user.setPasswordHash(passwordEncoder.encode(newValue));
                break;
                
            default:
                return false;
        }
        
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ユーザー削除
     */
    public boolean deleteUser(Long userId) {
        try {
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ユーザー名の妥当性チェック
     */
    public boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty() && username.length() <= 50;
    }

    /**
     * メールアドレスの妥当性チェック
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // 簡単なメールアドレス形式チェック
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }

    /**
     * パスワードの妥当性チェック
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 100;
    }

    /**
     * 役割の妥当性チェック
     */
    public boolean isValidRole(String role) {
        return "admin".equals(role) || "employee".equals(role);
    }
}