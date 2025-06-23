package com.teama.javaproject.service;

import org.springframework.beans.factory.annotation.Autowired; 

import org.springframework.jdbc.core.JdbcTemplate; 

import org.springframework.stereotype.Service; 

import org.springframework.transaction.annotation.Transactional; 

import java.time.LocalDate; 

import java.time.format.TextStyle; 

import java.util.Locale; 

import java.util.List; 

import java.util.ArrayList;

import java.util.Map; 

import java.util.HashMap; 

 

@Service 

@Transactional 

public class SalesService { 

 

    @Autowired 

    private JdbcTemplate jdbcTemplate; 

 

    public void registerSalesData(String salesDate, Integer salesId, Map<Long, Integer> productSales, Integer createBy) { 

        try { 

            System.out.println("=== サービス層での売上登録開始 ==="); 

             

            // 1. 日付文字列をLocalDateに変換 

            LocalDate date = LocalDate.parse(salesDate); 

            System.out.println("変換後の日付: " + date); 

             

            // 2. 曜日を取得 

            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.JAPANESE); 

            System.out.println("曜日: " + dayOfWeek); 

             

            // 3. 総売上金額と総販売数を計算 

            int totalRevenue = 0; 

            int totalCups = 0; 

             

            for (Map.Entry<Long, Integer> entry : productSales.entrySet()) { 

                Long productId = entry.getKey(); 

                Integer quantity = entry.getValue(); 

                 

                // 商品の単価を取得 

                Integer unitPrice = jdbcTemplate.queryForObject( 

                    "SELECT unit_price FROM products WHERE id = ?",  

                    Integer.class,  

                    productId 

                ); 

                 

                if (unitPrice == null) { 

                    throw new RuntimeException("商品ID " + productId + " の単価が見つかりません"); 

                } 

                 

                int revenue = unitPrice * quantity; 

                totalRevenue += revenue; 

                totalCups += quantity; 

                 

                System.out.println("商品ID: " + productId + ", 数量: " + quantity + ", 単価: " + unitPrice + ", 売上: " + revenue); 

            } 

             

            System.out.println("総売上金額: " + totalRevenue + ", 総販売数: " + totalCups); 

             

            // 4. daily_beer_summary テーブルに日次サマリーを保存 

            String summaryInsertSql = """ 

                INSERT INTO daily_beer_summary (sales_id, date, day_of_week, customer_count, total_cups, total_revenue)  

                VALUES (?, ?, ?, ?, ?, ?) 

            """; 

             

            int summaryResult = jdbcTemplate.update(summaryInsertSql,  

                salesId, date, dayOfWeek, null, totalCups, totalRevenue); 

             

            System.out.println("サマリーテーブル挿入結果: " + summaryResult); 

             

            // 5. daily_beer_sales テーブルに商品別売上明細を保存 

            String detailInsertSql = """ 

                INSERT INTO daily_beer_sales (sales_id, product_id, quantity, revenue, create_by)  

                VALUES (?, ?, ?, ?, ?) 

            """; 

             

            for (Map.Entry<Long, Integer> entry : productSales.entrySet()) { 

                Long productId = entry.getKey(); 

                Integer quantity = entry.getValue(); 

                 

                // 再度単価を取得（効率化のため事前に取得済みの値を使用することも可能） 

                Integer unitPrice = jdbcTemplate.queryForObject( 

                    "SELECT unit_price FROM products WHERE id = ?",  

                    Integer.class,  

                    productId 

                ); 

                 

                int revenue = unitPrice * quantity; 

                 

                int detailResult = jdbcTemplate.update(detailInsertSql,  

                    salesId, productId, quantity, revenue, createBy); 

                 

                System.out.println("明細テーブル挿入結果 (商品ID:" + productId + "): " + detailResult); 

            } 

             

            System.out.println("=== サービス層での売上登録完了 ==="); 

             

        } catch (Exception e) { 

            System.err.println("サービス層でのエラー: " + e.getMessage()); 

            e.printStackTrace(); 

            throw new RuntimeException("売上データの保存に失敗しました: " + e.getMessage(), e); 

        } 

    } 

     

    public boolean existsBySalesId(Integer salesId) { 

        try { 

            String sql = "SELECT COUNT(*) FROM daily_beer_summary WHERE sales_id = ?"; 

            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, salesId); 

            return count != null && count > 0; 

        } catch (Exception e) { 

            System.err.println("販売ID存在チェックエラー: " + e.getMessage()); 

            return false; 

        } 

    } 

     

    public List<Map<String, Object>> getSalesHistory() { 

        try { 

            // まず、簡単なクエリでデータの存在を確認 

            String countSql = "SELECT COUNT(*) FROM daily_beer_summary"; 

            Integer totalCount = jdbcTemplate.queryForObject(countSql, Integer.class); 

            System.out.println("daily_beer_summaryのデータ件数: " + totalCount); 

             

            if (totalCount == 0) { 

                System.out.println("daily_beer_summaryテーブルにデータがありません"); 

                return new ArrayList<>(); 

            } 

             

            // 売上履歴を取得（シンプル版） 

            String sql = """ 

                SELECT  

                    sales_id, 

                    date, 

                    day_of_week, 

                    customer_count, 

                    total_cups, 

                    total_revenue 

                FROM daily_beer_summary 

                ORDER BY date DESC, sales_id DESC 

            """; 

             

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql); 

            System.out.println("売上履歴クエリ結果件数: " + results.size()); 

             

            // 取得したデータの詳細をログ出力 

            for (Map<String, Object> row : results) { 

                System.out.println("売上データ: " + row); 

            } 

             

            return results; 

        } catch (Exception e) { 

            System.err.println("売上履歴取得エラー: " + e.getMessage()); 

            e.printStackTrace(); 

            throw new RuntimeException("売上履歴の取得に失敗しました: " + e.getMessage(), e); 

        } 

    } 

     

    public List<Map<String, Object>> getSalesHistoryByDate(String searchDate) { 

        try { 

            System.out.println("日付での売上履歴検索（元の日付）: " + searchDate); 

             

            // 日付フォーマットを変換（2025/06/23 → 2025-06-23） 

            String convertedDate = searchDate; 

            if (searchDate.contains("/")) { 

                convertedDate = searchDate.replace("/", "-"); 

            } 

            System.out.println("変換後の日付: " + convertedDate); 

             

            // まず、指定日付のデータ件数を確認 

            String countSql = "SELECT COUNT(*) FROM daily_beer_summary WHERE date = ?"; 

            Integer count = jdbcTemplate.queryForObject(countSql, Integer.class, convertedDate); 

            System.out.println("指定日付のデータ件数: " + count); 

             

            // さらにデバッグ：データベースの全日付を確認 

            String allDatesSql = "SELECT DISTINCT date FROM daily_beer_summary ORDER BY date"; 

            List<String> allDates = jdbcTemplate.queryForList(allDatesSql, String.class); 

            System.out.println("データベース内の全日付: " + allDates); 

             

            if (count == 0) { 

                System.out.println("指定日付のデータが見つかりません: " + convertedDate); 

                return new ArrayList<>(); 

            } 

             

            String sql = """ 

                SELECT  

                    sales_id, 

                    date, 

                    day_of_week, 

                    customer_count, 

                    total_cups, 

                    total_revenue 

                FROM daily_beer_summary 

                WHERE date = ? 

                ORDER BY sales_id DESC 

            """; 

             

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, convertedDate); 

            System.out.println("日付検索結果件数: " + results.size()); 

             

            // 取得したデータの詳細をログ出力 

            for (Map<String, Object> row : results) { 

                System.out.println("検索結果データ: " + row); 

            } 

             

            return results; 

        } catch (Exception e) { 

            System.err.println("日付検索エラー: " + e.getMessage()); 

            e.printStackTrace(); 

            throw new RuntimeException("日付検索に失敗しました: " + e.getMessage(), e); 

        } 

    } 

} 