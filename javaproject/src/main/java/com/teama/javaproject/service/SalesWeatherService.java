package com.teama.javaproject.service;

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
     * 一週間分の販売実績データを取得
     */
    public Map<String, List<Integer>> getWeeklySalesData(LocalDate startDate) {
        System.out.println("=== 販売データ検索 ===");
        System.out.println("販売データ基準日: " + startDate);
        
        // 日付から sales_id を計算（2024年4月1日 = sales_id 1 と仮定）
        LocalDate baseDate = LocalDate.of(2024, 4, 1);
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(baseDate, startDate);
        int startSalesId = Math.max(1, (int) daysDiff + 1);
        int endSalesId = startSalesId + 6;
        
        System.out.println("計算された sales_id 範囲: " + startSalesId + " ～ " + endSalesId);
        
        // データベースから販売データを取得
        List<Object[]> salesData = salesRepository.findDailySalesDataByProductAndSalesIdRange(startSalesId, endSalesId);
        
        System.out.println("取得した販売データ件数: " + salesData.size());
        
        // データを整理（商品名 -> 日別販売数量のマップ）
        Map<String, List<Integer>> chartData = new HashMap<>();
        
        // 商品名の初期化
        String[] productNames = {"ホワイトビール", "ラガー", "ペールエール", "フルーツビール", "黒ビール", "IPA"};
        for (String productName : productNames) {
            chartData.put(productName, new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0)));
        }
        
        // 実際のデータをマップに設定
        for (Object[] row : salesData) {
            Integer salesId = (Integer) row[0];
            String productName = (String) row[1];
            Long quantity = (Long) row[2];
            
            int dayIndex = salesId - startSalesId;
            if (dayIndex >= 0 && dayIndex < 7 && chartData.containsKey(productName)) {
                chartData.get(productName).set(dayIndex, quantity.intValue());
                System.out.println("販売データ設定: " + productName + 
                                 " - Day" + dayIndex + " - " + quantity + "本");
            }
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