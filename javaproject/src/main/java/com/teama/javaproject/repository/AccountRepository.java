package com.teama.javaproject.repository;

import com.teama.javaproject.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * アカウントリポジトリ
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * ユーザー名でアカウントを検索
     */
    Optional<Account> findByUsername(String username);
    
    /**
     * メールアドレスでアカウントを検索
     */
    Optional<Account> findByEmail(String email);
    
    /**
     * ユーザー名の存在確認
     */
    boolean existsByUsername(String username);
    
    /**
     * メールアドレスの存在確認
     */
    boolean existsByEmail(String email);
    
    /**
     * ユーザー名で部分一致検索
     */
    List<Account> findByUsernameContainingIgnoreCase(String username);
    
    /**
     * メールアドレスで部分一致検索
     */
    List<Account> findByEmailContainingIgnoreCase(String email);
}