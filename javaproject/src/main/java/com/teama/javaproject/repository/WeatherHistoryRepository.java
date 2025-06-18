package com.teama.javaproject.repository;

import com.teama.javaproject.entity.WeatherHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherHistoryRepository extends JpaRepository<WeatherHistory, Long> {
    // 必要なら後でカスタムクエリ追加
}
