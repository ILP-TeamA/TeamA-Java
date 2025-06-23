package com.teama.javaproject.repository;

import com.teama.javaproject.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    
    // 指定日付の予測データを取得
    List<Prediction> findByPredictionDate(LocalDate date);
    
    // 指定日付の予測データを削除（重複防止用）
    @Modifying
    @Transactional
    @Query("DELETE FROM Prediction p WHERE p.predictionDate = :date")
    void deleteByPredictionDate(@Param("date") LocalDate date);
    
    // 指定日付の予測データ存在チェック
    boolean existsByPredictionDate(LocalDate date);
    
    // 商品別予測データ取得
    @Query("SELECT p FROM Prediction p WHERE p.predictionDate = :date ORDER BY p.product.id ASC")
    List<Prediction> findByPredictionDateOrderByProductId(@Param("date") LocalDate date);
}