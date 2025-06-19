package com.teama.javaproject.repository;
import com.teama.javaproject.entity.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {
    // 日付別売上データ取得
    List<SalesRecord> findBySalesDateOrderByProductIdAsc(LocalDate salesDate);
    
    // 日付範囲での売上データ取得
    List<SalesRecord> findBySalesDateBetweenOrderBySalesDateDescProductIdAsc(
        LocalDate startDate, LocalDate endDate);
    
    // 日付別売上合計取得
    @Query("SELECT sr FROM SalesRecord sr WHERE sr.salesDate = :date ORDER BY sr.salesDate DESC")
    List<SalesRecord> findSalesByDate(@Param("date") LocalDate date);
    
    // 重複チェック用
    boolean existsBySalesDateAndProduct_Id(LocalDate salesDate, Long productId);
}