package com.teama.javaproject.repository;

import com.teama.javaproject.entity.DailyBeerSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 日別ビール販売データリポジトリ
 */
@Repository
public interface DailyBeerSalesRepository extends JpaRepository<DailyBeerSales, Long> {
    
    /**
     * 指定した販売IDの範囲で販売データを取得
     */
    List<DailyBeerSales> findBySalesIdBetweenOrderBySalesId(Integer startSalesId, Integer endSalesId);
    
    /**
     * 商品別・期間別の販売数量を集計
     */
    @Query("SELECT p.name, SUM(dbs.quantity) as totalQuantity " +
           "FROM DailyBeerSales dbs " +
           "JOIN dbs.product p " +
           "WHERE dbs.salesId BETWEEN :startSalesId AND :endSalesId " +
           "GROUP BY p.name " +
           "ORDER BY p.name")
    List<Object[]> findSalesQuantityByProductAndSalesIdRange(@Param("startSalesId") Integer startSalesId, 
                                                             @Param("endSalesId") Integer endSalesId);
    
    /**
     * 日別・商品別の販売数量を取得（Chart.js用データ）
     */
    @Query("SELECT dbs.salesId, p.name, SUM(dbs.quantity) as dailyQuantity " +
           "FROM DailyBeerSales dbs " +
           "JOIN dbs.product p " +
           "WHERE dbs.salesId BETWEEN :startSalesId AND :endSalesId " +
           "GROUP BY dbs.salesId, p.name " +
           "ORDER BY dbs.salesId, p.name")
    List<Object[]> findDailySalesDataByProductAndSalesIdRange(@Param("startSalesId") Integer startSalesId, 
                                                              @Param("endSalesId") Integer endSalesId);
    
    /**
     * 指定販売IDの商品別合計を取得
     */
    @Query("SELECT p.name, SUM(dbs.quantity) " +
           "FROM DailyBeerSales dbs " +
           "JOIN dbs.product p " +
           "WHERE dbs.salesId = :salesId " +
           "GROUP BY p.name")
    List<Object[]> findProductSalesBySalesId(@Param("salesId") Integer salesId);
}