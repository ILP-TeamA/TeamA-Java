package com.teama.javaproject.service;

import com.teama.javaproject.entity.*;
import com.teama.javaproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class WeeklyDetailService {
    
    @Autowired
    private WeatherHistoryRepository weatherHistoryRepository;
    
    @Autowired
    private DailyBeerSummaryRepository dailyBeerSummaryRepository;
    
    @Autowired
    private DailyBeerSalesRepository dailyBeerSalesRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    // 月の週一覧取得
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
                
                // 週の売上サマリ取得
                Map<String, Object> weekSummary = getWeekSummary(weekStart, weekEnd);
                
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
    
    // 週詳細データ取得
    public Map<String, Object> getWeeklyDetail(LocalDate weekStart, LocalDate weekEnd) {
        Map<String, Object> weeklyDetail = new HashMap<>();
        
        // 日別データリスト
        List<Map<String, Object>> dailyData = new ArrayList<>();
        
        LocalDate currentDate = weekStart;
        while (!currentDate.isAfter(weekEnd)) {
            Map<String, Object> dayData = getDayDetail(currentDate);
            dailyData.add(dayData);
            currentDate = currentDate.plusDays(1);
        }
        
        // 週全体のサマリ
        Map<String, Object> weekSummary = getWeekSummary(weekStart, weekEnd);
        
        weeklyDetail.put("dailyData", dailyData);
        weeklyDetail.put("weekSummary", weekSummary);
        weeklyDetail.put("weekStart", weekStart);
        weeklyDetail.put("weekEnd", weekEnd);
        
        return weeklyDetail;
    }
    
    // 特定日の詳細データ取得
    public Map<String, Object> getDayDetail(LocalDate date) {
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
            // 風速データがある場合（WeatherHistoryエンティティに追加が必要）
            weatherData.put("windSpeed", getWindSpeed(w)); // カスタムメソッド
            
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
    
    // 週サマリ計算
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
}
