package com.teama.javaproject.service;

import com.teama.javaproject.entity.DailyBeerSales;
import com.teama.javaproject.entity.WeatherHistory;
import com.teama.javaproject.repository.DailyBeerSalesRepository;
import com.teama.javaproject.repository.WeatherHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    /**
     * 一週間分の天気データを取得
     */
    public List<WeatherHistory> getWeeklyWeatherData(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        
        // データベースから実際のデータを取得
        List<WeatherHistory> weatherData = weatherRepository.findByDateBetweenOrderByDate(startDate, endDate);
        
        // データが不足している場合、利用可能な最新日付を確認
        if (weatherData.isEmpty()) {
            Optional<LocalDate> latestDate = weatherRepository.findLatestAvailableDate();
            if (latestDate.isPresent()) {
                // 最新データが2025年6月18日以前の場合、その範囲でデータを取得
                LocalDate adjustedEndDate = latestDate.get();
                LocalDate adjustedStartDate = adjustedEndDate.minusDays(6);
                weatherData = weatherRepository.findByDateBetweenOrderByDate(adjustedStartDate, adjustedEndDate);
            }
        }
        
        return weatherData;
    }

    /**
     * 一週間分の販売実績データを取得
     */
    public Map<String, List<Integer>> getWeeklySalesData(LocalDate startDate) {
        // 日付から sales_id を推定（仮定：2025年6月1日 = sales_id 1）
        LocalDate baseDate = LocalDate.of(2025, 6, 1);
        int daysDiff = (int) java.time.temporal.ChronoUnit.DAYS.between(baseDate, startDate);
        int startSalesId = Math.max(1, daysDiff + 1);
        int endSalesId = startSalesId + 6;
        
        // データベースから販売データを取得
        List<Object[]> salesData = salesRepository.findDailySalesDataByProductAndSalesIdRange(startSalesId, endSalesId);
        
        // データを整理（商品名 -> 日別販売数量のマップ）
        Map<String, List<Integer>> chartData = new HashMap<>();
        
        // 7日分の初期化
        for (int i = 0; i < 7; i++) {
            // 各商品の初期化（主要なビール種類）
            initializeProductData(chartData, "ホワイトビール");
            initializeProductData(chartData, "ラガー");
            initializeProductData(chartData, "ペールエール");
            initializeProductData(chartData, "フルーツビール");
            initializeProductData(chartData, "黒ビール");
            initializeProductData(chartData, "IPA");
        }
        
        // 実際のデータをマップに設定
        for (Object[] row : salesData) {
            Integer salesId = (Integer) row[0];
            String productName = (String) row[1];
            Long quantity = (Long) row[2];
            
            int dayIndex = salesId - startSalesId;
            if (dayIndex >= 0 && dayIndex < 7) {
                if (chartData.containsKey(productName)) {
                    chartData.get(productName).set(dayIndex, quantity.intValue());
                }
            }
        }
        
        return chartData;
    }
    
    /**
     * 商品データの初期化
     */
    private void initializeProductData(Map<String, List<Integer>> chartData, String productName) {
        if (!chartData.containsKey(productName)) {
            chartData.put(productName, Arrays.asList(0, 0, 0, 0, 0, 0, 0));
        }
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
        
        return labels;
    }

    /**
     * Chart.js用のデータセットを生成
     */
    public String generateChartDatasets(Map<String, List<Integer>> salesData) {
        StringBuilder datasets = new StringBuilder("[");
        
        // ビール種類と色の定義
        Map<String, String> colorMap = new HashMap<>();
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
     * データベースにデータが存在するかチェック
     */
    public boolean hasWeatherData(LocalDate startDate, LocalDate endDate) {
        return weatherRepository.existsByDateRange(startDate, endDate);
    }
    
    /**
     * 利用可能な最新の天気データ日付を取得
     */
    public Optional<LocalDate> getLatestWeatherDataDate() {
        return weatherRepository.findLatestAvailableDate();
    }
}