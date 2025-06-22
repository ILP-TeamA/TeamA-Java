package com.teama.javaproject.controller;

import com.teama.javaproject.entity.User;
import com.teama.javaproject.service.AccountManagementService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountManagementController {

    @Autowired
    private AccountManagementService accountManagementService;

    /**
     * アカウント管理メニュー画面表示
     */
    @GetMapping("/management")
    public String showAccountManagement(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        
        // ログインチェック
        if (user == null) {
            return "redirect:/login";
        }
        
        // 管理者権限チェック
        if (!"admin".equals(user.getRole())) {
            return "redirect:/home";
        }
        
        model.addAttribute("username", user.getUsername());
        return "account-management-menu";
    }

    /**
     * アカウント作成画面表示
     */
    @GetMapping("/create")
    public String showCreateAccount(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        
        // ログインチェック & 管理者権限チェック
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        return "account-create";
    }

    /**
     * アカウント作成処理
     */
    @PostMapping("/create")
    public String createAccount(@RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String role,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        
        User user = (User) session.getAttribute("loginUser");
        
        // ログインチェック & 管理者権限チェック
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        try {
            boolean result = accountManagementService.createAccount(username, email, password, role);
            
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "アカウント「" + username + "」を正常に作成しました");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "このメールアドレスは既に使用されています");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "アカウント作成中にエラーが発生しました: " + e.getMessage());
        }
        
        return "redirect:/account/create";
    }

    /**
     * アカウント検索・編集画面表示
     */
    @GetMapping("/search")
    public String showSearchAccount(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        
        // ログインチェック & 管理者権限チェック
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        return "account-search";
    }

    /**
     * アカウント検索処理
     */
    @PostMapping("/search")
    public String searchAccount(@RequestParam String searchEmail,
                              Model model,
                              HttpSession session) {
        
        User user = (User) session.getAttribute("loginUser");
        
        // ログインチェック & 管理者権限チェック
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        try {
            Optional<User> foundUser = accountManagementService.findUserByEmail(searchEmail);
            
            if (foundUser.isPresent()) {
                model.addAttribute("foundUser", foundUser.get());
                model.addAttribute("searchEmail", searchEmail);
            } else {
                model.addAttribute("errorMessage", "該当するアカウントが見つかりませんでした");
                model.addAttribute("searchEmail", searchEmail);
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "検索中にエラーが発生しました: " + e.getMessage());
        }
        
        return "account-search";
    }

    /**
     * ユーザー名編集画面表示
     */
    @GetMapping("/edit/username/{userId}")
    public String showEditUsername(@PathVariable Long userId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        Optional<User> targetUser = accountManagementService.findUserById(userId);
        if (targetUser.isPresent()) {
            model.addAttribute("targetUser", targetUser.get());
            model.addAttribute("editType", "username");
            return "account-edit-field";
        } else {
            return "redirect:/account/search";
        }
    }

    /**
     * メール編集画面表示
     */
    @GetMapping("/edit/email/{userId}")
    public String showEditEmail(@PathVariable Long userId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        Optional<User> targetUser = accountManagementService.findUserById(userId);
        if (targetUser.isPresent()) {
            model.addAttribute("targetUser", targetUser.get());
            model.addAttribute("editType", "email");
            return "account-edit-field";
        } else {
            return "redirect:/account/search";
        }
    }

    /**
     * パスワード編集画面表示
     */
    @GetMapping("/edit/password/{userId}")
    public String showEditPassword(@PathVariable Long userId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        Optional<User> targetUser = accountManagementService.findUserById(userId);
        if (targetUser.isPresent()) {
            model.addAttribute("targetUser", targetUser.get());
            model.addAttribute("editType", "password");
            return "account-edit-field";
        } else {
            return "redirect:/account/search";
        }
    }

    /**
     * フィールド編集処理
     */
    @PostMapping("/edit/{editType}/{userId}")
    public String updateField(@PathVariable String editType,
                            @PathVariable Long userId,
                            @RequestParam String newValue,
                            @RequestParam(required = false) String confirmValue,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        
        User user = (User) session.getAttribute("loginUser");
        
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        try {
            boolean result = accountManagementService.updateUserField(userId, editType, newValue, confirmValue);
            
            if (result) {
                String fieldName = getFieldDisplayName(editType);
                redirectAttributes.addFlashAttribute("successMessage", 
                    fieldName + "を正常に更新しました");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "更新に失敗しました。入力内容を確認してください");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "更新中にエラーが発生しました: " + e.getMessage());
        }
        
        return "redirect:/account/search";
    }

    /**
     * アカウント削除処理
     */
    @PostMapping("/delete/{userId}")
    public String deleteAccount(@PathVariable Long userId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        
        User user = (User) session.getAttribute("loginUser");
        
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        try {
            boolean result = accountManagementService.deleteUser(userId);
            
            if (result) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "アカウントを正常に削除しました");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "アカウントの削除に失敗しました");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "削除中にエラーが発生しました: " + e.getMessage());
        }
        
        return "redirect:/account/search";
    }

    /**
     * フィールド名の表示用変換
     */
    private String getFieldDisplayName(String editType) {
        switch (editType) {
            case "username": return "ユーザー名";
            case "email": return "メールアドレス";
            case "password": return "パスワード";
            default: return "項目";
        }
    }
}
