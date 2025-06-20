package com.teama.javaproject.repository;

import com.teama.javaproject.entity.WeatherHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherHistoryRepository extends JpaRepository<WeatherHistory, Integer> {
    
    /**
     * 指定した日付範囲の天気データを取得
     */
    List<WeatherHistory> findByDateBetweenOrderByDate(LocalDate startDate, LocalDate endDate);
    
    /**
     * 指定日の天気データを取得
     */
    Optional<WeatherHistory> findByDate(LocalDate date);
    
    /**
     * 利用可能な最新の日付を取得
     */
    @Query("SELECT MAX(wh.date) FROM WeatherHistory wh")
    Optional<LocalDate> findLatestAvailableDate();
    
    /**
     * 指定日以前で最も近い日付のデータを取得
     */
    @Query("SELECT wh FROM WeatherHistory wh WHERE wh.date <= :targetDate ORDER BY wh.date DESC")
    List<WeatherHistory> findLatestBeforeDate(@Param("targetDate") LocalDate targetDate);
    
    /**
     * データが存在する日付の範囲を確認
     */
    @Query("SELECT COUNT(wh) > 0 FROM WeatherHistory wh WHERE wh.date BETWEEN :startDate AND :endDate")
    boolean existsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
