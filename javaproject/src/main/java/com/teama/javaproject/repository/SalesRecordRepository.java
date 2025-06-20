package com.teama.javaproject.repository;

import com.teama.javaproject.entity.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {
    
    // 販売IDでの売上データ取得（商品ID昇順）
    List<SalesRecord> findBySalesIdOrderByProduct_IdAsc(Integer salesId);
    
    // 全件取得（販売ID降順、商品ID昇順）
    List<SalesRecord> findAllByOrderBySalesIdDescProduct_IdAsc();
    
    // 商品IDでの売上データ取得（販売ID降順）
    List<SalesRecord> findByProduct_IdOrderBySalesIdDesc(Long productId);
    
    // 販売IDと商品IDでの重複チェック
    boolean existsBySalesIdAndProduct_Id(Integer salesId, Long productId);
    
    // 販売IDでの売上合計取得（カスタムクエリ）
    @Query("SELECT sr FROM SalesRecord sr WHERE sr.salesId = :salesId ORDER BY sr.product.id ASC")
    List<SalesRecord> findSalesBySalesId(@Param("salesId") Integer salesId);
    
    // 特定の登録者による売上データ取得
    List<SalesRecord> findByCreateByOrderBySalesIdDesc(Integer createBy);
    
    // 売上金額の範囲での検索
    List<SalesRecord> findByRevenueBetweenOrderBySalesIdDesc(Integer minRevenue, Integer maxRevenue);
    
    // 販売数量での検索
    List<SalesRecord> findByQuantityGreaterThanEqualOrderBySalesIdDesc(Integer quantity);
}