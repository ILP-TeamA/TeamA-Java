package com.teama.javaproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()  // すべてのリクエスト許可
            )
            .csrf().disable()  // POST 許可のため CSRF 無効化
            .formLogin().disable(); // ログイン画面 無効化

        return http.build();
    }
}

