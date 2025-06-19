// package com.teama.javaproject.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// import java.util.Optional;

// import com.teama.javaproject.entity.User;
// import com.teama.javaproject.repository.UserRepository;

// @Service
// public class LoginServiceImpl implements LoginService {

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public boolean login(String email, String password) {
//         Optional<User> userOpt = userRepository.findByEmail(email);
//         if (userOpt.isPresent()) {
//             return new BCryptPasswordEncoder().matches(password, userOpt.get().getPasswordHash());
//         }
//         return false;
//     }
// }