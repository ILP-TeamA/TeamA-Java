// package com.teama.javaproject.entity;

// import jakarta.persistence.Entity;
// import jakarta.persistence.Table;
// import jakarta.persistence.Id;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;

// @Entity
// @Table(name = "users")
// public class User {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long userId;

//     private String username;
//     private String email;
//     private String passwordHash;
//     private String role;

//     // Getter & Setter
//     public String getUsername() {
//         return username;
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getEmail() {
//         return email;
//     }

//     public void setEmail(String email) {
//         this.email = email;
//     }

//     public String getPasswordHash() {
//         return passwordHash;
//     }

//     public void setPasswordHash(String passwordHash) {
//         this.passwordHash = passwordHash;
//     }

//     public String getRole() {
//         return role;
//     }

//     public void setRole(String role) {
//         this.role = role;
//     }
// }
