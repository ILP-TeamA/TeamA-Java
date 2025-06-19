package com.teama.javaproject.service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WeeklyDetailService {
    
    // TODO: 以下のRepositoryは後でRepository作成後に有効化
    /*
    @Autowired
    private WeatherHistoryRepository weatherHistoryRepository;
    
    @Autowired
    private DailyBeerSummaryRepository dailyBeerSummaryRepository;
    
    @Autowired
    private DailyBeerSalesRepository dailyBeerSalesRepository;
    
    @Autowired
    private ProductRepository productRepository;
    */
    
    // 月の週一覧取得（ダミーデータ版）
    public List<Map<String, Object>> getMonthlyWeeks(int year, int month) {
        List<Map<String, Object>> weeks = new ArrayList<>();
        
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        
        LocalDate currentDate = firstDayOfMonth;
        Set<Integer> processedWeeks = new HashSet<>();
        
        while (!currentDate.isAfter(lastDayOfMonth)) {
            int weekOfYear = currentDate.get(weekFields.weekOfYear());
            
            if (!processedWeeks.contains(weekOfYear)) {
                LocalDate weekStart = currentDate.with(weekFields.dayOfWeek(), 1);
                LocalDate weekEnd = weekStart.plusDays(6);
                
                // 週の売上サマリ取得（ダミーデータ）
                Map<String, Object> weekSummary = getWeekSummaryDummy(weekStart, weekEnd);
                
                Map<String, Object> weekData = new HashMap<>();
                weekData.put("weekStart", weekStart);
                weekData.put("weekEnd", weekEnd);
                weekData.put("weekOfYear", weekOfYear);
                weekData.put("totalSales", weekSummary.get("totalSales"));
                weekData.put("totalCups", weekSummary.get("totalCups"));
                weekData.put("averageTemperature", weekSummary.get("averageTemperature"));
                weekData.put("rainyDays", weekSummary.get("rainyDays"));
                
                weeks.add(weekData);
                processedWeeks.add(weekOfYear);
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        return weeks;
    }
    
    // 週詳細データ取得（ダミーデータ版）
    public Map<String, Object> getWeeklyDetail(LocalDate weekStart, LocalDate weekEnd) {
        Map<String, Object> weeklyDetail = new HashMap<>();
        
        // 日別データリスト
        List<Map<String, Object>> dailyData = new ArrayList<>();
        
        LocalDate currentDate = weekStart;
        while (!currentDate.isAfter(weekEnd)) {
            Map<String, Object> dayData = getDayDetailDummy(currentDate);
            dailyData.add(dayData);
            currentDate = currentDate.plusDays(1);
        }
        
        // 週全体のサマリ
        Map<String, Object> weekSummary = getWeekSummaryDummy(weekStart, weekEnd);
        
        weeklyDetail.put("dailyData", dailyData);
        weeklyDetail.put("weekSummary", weekSummary);
        weeklyDetail.put("weekStart", weekStart);
        weeklyDetail.put("weekEnd", weekEnd);
        
        return weeklyDetail;
    }
    
    // 特定日の詳細データ取得（ダミーデータ版）
    public Map<String, Object> getDayDetail(LocalDate date) {
        return getDayDetailDummy(date);
    }
    
    // ダミーデータ：特定日の詳細
    private Map<String, Object> getDayDetailDummy(LocalDate date) {
        Map<String, Object> dayDetail = new HashMap<>();
        Random random = new Random(date.hashCode()); // 日付をシードにして一貫性を保つ
        
        // 基本情報
        dayDetail.put("date", date);
        dayDetail.put("dayOfWeek", date.getDayOfWeek().getDisplayName(
            java.time.format.TextStyle.SHORT, Locale.JAPANESE));
        
        // ダミー天気データ
        String[] weatherConditions = {"☀️", "☁️", "🌦️", "🌧️"};
        String[] conditionNames = {"晴れ", "曇り", "小雨", "雨"};
        int weatherIndex = random.nextInt(weatherConditions.length);
        
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("condition", conditionNames[weatherIndex]);
        weatherData.put("icon", weatherConditions[weatherIndex]);
        weatherData.put("maxTemperature", random.nextInt(15) + 15); // 15-30°C
        weatherData.put("minTemperature", random.nextInt(10) + 5);  // 5-15°C
        weatherData.put("avgTemperature", random.nextInt(10) + 18); // 18-28°C
        weatherData.put("rainfall", random.nextDouble() * 20);      // 0-20mm
        weatherData.put("humidity", random.nextInt(40) + 40);       // 40-80%
        weatherData.put("sunshineHours", random.nextDouble() * 12); // 0-12時間
        weatherData.put("windSpeed", Math.round((random.nextDouble() * 8 + 1) * 10) / 10.0); // 1-9 m/s
        
        dayDetail.put("weather", weatherData);
        
        // ダミー売上データ
        int totalCups = random.nextInt(200) + 50; // 50-250杯
        double avgPrice = 800 + random.nextDouble() * 400; // 800-1200円
        double totalRevenue = totalCups * avgPrice;
        
        Map<String, Object> salesData = new HashMap<>();
        salesData.put("totalCups", totalCups);
        salesData.put("totalRevenue", Math.round(totalRevenue));
        
        // ダミー商品別データ
        String[] products = {"ペールエール", "IPA", "スタウト", "ラガー"};
        double[] prices = {1000, 1200, 1100, 900};
        List<Map<String, Object>> productSales = new ArrayList<>();
        
        for (int i = 0; i < products.length; i++) {
            int quantity = random.nextInt(totalCups / 4) + 5; // 各商品の売上
            double revenue = quantity * prices[i];
            
            Map<String, Object> productData = new HashMap<>();
            productData.put("productName", products[i]);
            productData.put("quantity", quantity);
            productData.put("revenue", Math.round(revenue));
            productData.put("unitPrice", prices[i]);
            productSales.add(productData);
        }
        
        salesData.put("productSales", productSales);
        dayDetail.put("sales", salesData);
        
        return dayDetail;
    }
    
    // ダミーデータ：週サマリ計算
    private Map<String, Object> getWeekSummaryDummy(LocalDate weekStart, LocalDate weekEnd) {
        Map<String, Object> summary = new HashMap<>();
        Random random = new Random(weekStart.hashCode());
        
        // ダミー売上サマリ
        int totalCups = random.nextInt(1000) + 500; // 500-1500杯/週
        double totalSales = totalCups * (800 + random.nextDouble() * 400); // 適当な平均単価
        
        // ダミー天気サマリ
        double avgTemperature = 15 + random.nextDouble() * 15; // 15-30°C
        long rainyDays = random.nextInt(4); // 0-3日
        
        summary.put("totalCups", totalCups);
        summary.put("totalSales", Math.round(totalSales));
        summary.put("averageTemperature", Math.round(avgTemperature * 10) / 10.0);
        summary.put("rainyDays", rainyDays);
        summary.put("daysWithData", 7); // 常に7日分
        
        return summary;
    }
    
    /* 
    TODO: 以下のメソッドはRepository作成後に有効化
    実際のデータベース接続版のメソッド群
    
    // 週サマリ計算（実データ版）
    private Map<String, Object> getWeekSummary(LocalDate weekStart, LocalDate weekEnd) {
        Map<String, Object> summary = new HashMap<>();
        
        // 売上サマリ
        List<DailyBeerSummary> weekSales = dailyBeerSummaryRepository
            .findByDateBetweenOrderByDateDesc(weekStart, weekEnd);
        
        int totalCups = weekSales.stream().mapToInt(DailyBeerSummary::getTotalCups).sum();
        double totalSales = weekSales.stream()
            .mapToDouble(s -> s.getTotalRevenue().doubleValue()).sum();
        
        // 天気サマリ
        List<WeatherHistory> weekWeather = weatherHistoryRepository
            .findByDateBetweenOrderByDateDesc(weekStart, weekEnd);
        
        double avgTemperature = weekWeather.stream()
            .filter(w -> w.getAvgTemperature() != null)
            .mapToDouble(w -> w.getAvgTemperature().doubleValue())
            .average().orElse(0.0);
        
        long rainyDays = weekWeather.stream()
            .filter(w -> w.getTotalRainfall() != null && w.getTotalRainfall().doubleValue() > 0)
            .count();
        
        summary.put("totalCups", totalCups);
        summary.put("totalSales", Math.round(totalSales));
        summary.put("averageTemperature", Math.round(avgTemperature * 10) / 10.0);
        summary.put("rainyDays", rainyDays);
        summary.put("daysWithData", weekSales.size());
        
        return summary;
    }
    
    // 特定日の詳細データ取得（実データ版）
    public Map<String, Object> getDayDetailFromDB(LocalDate date) {
        Map<String, Object> dayDetail = new HashMap<>();
        
        // 基本情報
        dayDetail.put("date", date);
        dayDetail.put("dayOfWeek", date.getDayOfWeek().getDisplayName(
            java.time.format.TextStyle.SHORT, Locale.JAPANESE));
        
        // 天気データ取得
        Optional<WeatherHistory> weather = weatherHistoryRepository.findByDate(date);
        if (weather.isPresent()) {
            WeatherHistory w = weather.get();
            Map<String, Object> weatherData = new HashMap<>();
            weatherData.put("condition", determineWeatherCondition(w));
            weatherData.put("icon", getWeatherIcon(w));
            weatherData.put("maxTemperature", w.getMaxTemperature());
            weatherData.put("minTemperature", w.getMinTemperature());
            weatherData.put("avgTemperature", w.getAvgTemperature());
            weatherData.put("rainfall", w.getTotalRainfall());
            weatherData.put("humidity", w.getAvgHumidity());
            weatherData.put("sunshineHours", w.getSunshineHours());
            weatherData.put("windSpeed", getWindSpeed(w));
            
            dayDetail.put("weather", weatherData);
        } else {
            dayDetail.put("weather", Map.of("condition", "データなし", "icon", "❓"));
        }
        
        // 売上データ取得
        Optional<DailyBeerSummary> salesSummary = dailyBeerSummaryRepository.findByDate(date);
        if (salesSummary.isPresent()) {
            DailyBeerSummary summary = salesSummary.get();
            
            // 商品別売上詳細取得
            List<DailyBeerSales> salesDetails = dailyBeerSalesRepository.findByDailyBeerSummary(summary);
            
            Map<String, Object> salesData = new HashMap<>();
            salesData.put("totalCups", summary.getTotalCups());
            salesData.put("totalRevenue", summary.getTotalRevenue());
            
            // 商品別データ
            List<Map<String, Object>> productSales = salesDetails.stream()
                .map(sale -> {
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productName", sale.getProduct().getName());
                    productData.put("quantity", sale.getQuantity());
                    productData.put("revenue", sale.getRevenue());
                    productData.put("unitPrice", sale.getProduct().getUnitPrice());
                    return productData;
                })
                .collect(Collectors.toList());
            
            salesData.put("productSales", productSales);
            dayDetail.put("sales", salesData);
        } else {
            dayDetail.put("sales", Map.of("totalCups", 0, "totalRevenue", 0, "productSales", List.of()));
        }
        
        return dayDetail;
    }
    
    // ヘルパーメソッド
    private String determineWeatherCondition(WeatherHistory weather) {
        if (weather.getTotalRainfall() != null && weather.getTotalRainfall().doubleValue() > 10) {
            return "雨";
        } else if (weather.getTotalRainfall() != null && weather.getTotalRainfall().doubleValue() > 0) {
            return "小雨";
        } else if (weather.getSunshineHours() != null && weather.getSunshineHours().doubleValue() > 8) {
            return "晴れ";
        } else {
            return "曇り";
        }
    }
    
    private String getWeatherIcon(WeatherHistory weather) {
        String condition = determineWeatherCondition(weather);
        switch (condition) {
            case "晴れ": return "☀️";
            case "曇り": return "☁️";
            case "小雨": return "🌦️";
            case "雨": return "🌧️";
            default: return "❓";
        }
    }
    
    // 風速データ取得（WeatherHistoryに風速フィールドがない場合のダミー実装）
    private Double getWindSpeed(WeatherHistory weather) {
        // TODO: WeatherHistoryエンティティに風速フィールド追加後、実装
        // 現在はダミーデータを返す
        return Math.random() * 10; // 0-10 m/s のランダム値
    }
    */
}