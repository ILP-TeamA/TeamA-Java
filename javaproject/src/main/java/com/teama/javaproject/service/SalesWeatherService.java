package com.teama.javaproject.service;

import com.teama.javaproject.entity.WeatherHistory;
import com.teama.javaproject.repository.DailyBeerSalesRepository;
import com.teama.javaproject.repository.WeatherHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SalesWeatherService {

    @Autowired
    private DailyBeerSalesRepository salesRepository;
    
    @Autowired
    private WeatherHistoryRepository weatherRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 一週間分の天気データを取得
     */
    public List<WeatherHistory> getWeeklyWeatherData(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        
        System.out.println("=== 天気データ検索 ===");
        System.out.println("検索開始日: " + startDate);
        System.out.println("検索終了日: " + endDate);
        
        // データベースから実際のデータを取得
        List<WeatherHistory> weatherData = weatherRepository.findByDateBetweenOrderByDate(startDate, endDate);
        
        System.out.println("取得した天気データ件数: " + weatherData.size());
        
        // デバッグ：取得したデータの詳細を出力
        for (WeatherHistory weather : weatherData) {
            System.out.println("天気データ: " + weather.getDate() + 
                             " - " + weather.getWeatherConditionDay() + 
                             " - " + weather.getAvgTemperature() + "℃");
        }
        
        return weatherData;
    }

    /**
     * 一週間分の販売実績データを取得（修正版）
     */
    public Map<String, List<Integer>> getWeeklySalesData(LocalDate startDate) {
        System.out.println("=== 販売データ検索 ===");
        System.out.println("販売データ基準日: " + startDate);
        
        LocalDate endDate = startDate.plusDays(6);
        
        // 日付範囲で直接SQLクエリを実行
        String sql = """
            SELECT 
                dbs.sales_id,
                summary.date,
                p.name as product_name,
                COALESCE(dbs.quantity, 0) as quantity
            FROM daily_beer_summary summary
            LEFT JOIN daily_beer_sales dbs ON summary.sales_id = dbs.sales_id
            LEFT JOIN products p ON dbs.product_id = p.id
            WHERE summary.date BETWEEN ? AND ?
            ORDER BY summary.date, p.id
            """;
        
        List<Map<String, Object>> salesData = jdbcTemplate.queryForList(sql, startDate, endDate);
        
        System.out.println("取得した販売データ件数: " + salesData.size());
        
        // データを整理（商品名 -> 日別販売数量のマップ）
        Map<String, List<Integer>> chartData = new HashMap<>();
        
        // 商品名の初期化（7日分のデータを0で初期化）
        String[] productNames = {"ホワイトビール", "ラガー", "ペールエール", "フルーツビール", "黒ビール", "IPA"};
        for (String productName : productNames) {
            chartData.put(productName, new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0)));
        }
        
        // 実際のデータをマップに設定
        for (Map<String, Object> row : salesData) {
            LocalDate salesDate = ((java.sql.Date) row.get("date")).toLocalDate();
            String productName = (String) row.get("product_name");
            Integer quantity = (Integer) row.get("quantity");
            
            if (productName != null && quantity != null) {
                // 開始日からの日数を計算
                int dayIndex = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, salesDate);
                
                if (dayIndex >= 0 && dayIndex < 7 && chartData.containsKey(productName)) {
                    chartData.get(productName).set(dayIndex, quantity);
                    System.out.println("販売データ設定: " + productName + 
                                     " - " + salesDate + " (Day" + dayIndex + ") - " + quantity + "本");
                }
            }
        }
        
        // デバッグ：最終的なチャートデータを出力
        System.out.println("=== 最終チャートデータ ===");
        for (Map.Entry<String, List<Integer>> entry : chartData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        return chartData;
    }

    /**
     * Chart.js用のラベル（日付）を生成
     */
    public List<String> generateChartLabels(LocalDate startDate) {
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
        
        for (int i = 0; i < 7; i++) {
            labels.add(startDate.plusDays(i).format(formatter));
        }
        
        System.out.println("生成されたラベル: " + labels);
        return labels;
    }

    /**
     * Chart.js用のデータセットを生成
     */
    public String generateChartDatasets(Map<String, List<Integer>> salesData) {
        StringBuilder datasets = new StringBuilder("[");
        
        // ビール種類と色の定義
        Map<String, String> colorMap = new LinkedHashMap<>();
        colorMap.put("ホワイトビール", "#FF6B6B");
        colorMap.put("ラガー", "#4ECDC4");
        colorMap.put("ペールエール", "#45B7D1");
        colorMap.put("フルーツビール", "#96CEB4");
        colorMap.put("黒ビール", "#FFEAA7");
        colorMap.put("IPA", "#DDA0DD");
        
        boolean first = true;
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            String productName = entry.getKey();
            String color = entry.getValue();
            
            if (!first) datasets.append(",");
            first = false;
            
            List<Integer> data = salesData.getOrDefault(productName, Arrays.asList(0, 0, 0, 0, 0, 0, 0));
            
            datasets.append("{")
                   .append("\"label\": \"").append(productName).append("\",")
                   .append("\"data\": ").append(data.toString()).append(",")
                   .append("\"borderColor\": \"").append(color).append("\",")
                   .append("\"backgroundColor\": \"").append(color).append("\",")
                   .append("\"fill\": false,")
                   .append("\"tension\": 0.1")
                   .append("}");
        }
        
        datasets.append("]");
        
        System.out.println("生成されたデータセット: " + datasets.toString());
        return datasets.toString();
    }
    
    /**
     * 表示期間の文字列を生成
     */
    public String generateDisplayPeriod(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        return startDate.format(formatter) + " ～ " + endDate.format(formatter);
    }
    
    /**
     * 利用可能な最新の天気データ日付を取得
     */
    public Optional<LocalDate> getLatestWeatherDataDate() {
        return Optional.of(LocalDate.of(2025, 6, 19));
    }
}