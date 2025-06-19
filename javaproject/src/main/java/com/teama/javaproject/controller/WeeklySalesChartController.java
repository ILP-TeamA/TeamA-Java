
package com.teama.javaproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// TODO: 後でSessionとRepositoryを有効化
// import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/weekly")
public class WeeklySalesChartController {
    
    // 週別売上グラフ画面（セッション無効版）
    @GetMapping("/sales-chart")
    public String weeklySalesChart(Model model,
                                  @RequestParam(required = false) String startDate) {
        // TODO: 後でログイン認証を有効化
        /*
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        */
        
        // デフォルト期間設定（今週）
        LocalDate weekStart = startDate != null ? LocalDate.parse(startDate) : 
                              LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd);
        
        return "weekly/sales-chart";
    }
    
    // 週別売上データ取得API（AJAX用）- セッション無効版
    @GetMapping("/api/sales-data")
    @ResponseBody
    public Map<String, Object> getWeeklySalesData(@RequestParam String startDate) {
        // TODO: 後でログイン認証を有効化
        /*
        if (!isLoggedIn(session)) {
            return Map.of("error", "Unauthorized");
        }
        */
        
        try {
            LocalDate weekStart = LocalDate.parse(startDate);
            LocalDate weekEnd = weekStart.plusDays(6);
            
            Map<String, Object> salesData = createDummySalesData(weekStart, weekEnd);
            System.out.println("売上データ作成完了: " + salesData);
            return salesData;
            
        } catch (Exception e) {
            System.err.println("売上データエラー: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }
    
    // 天気データ取得API（AJAX用）- セッション無効版
    @GetMapping("/api/weather-data")
    @ResponseBody
    public Map<String, Object> getWeeklyWeatherData(@RequestParam String startDate) {
        // TODO: 後でログイン認証を有効化
        /*
        if (!isLoggedIn(session)) {
            return Map.of("error", "Unauthorized");
        }
        */
        
        try {
            LocalDate weekStart = LocalDate.parse(startDate);
            LocalDate weekEnd = weekStart.plusDays(6);
            
            Map<String, Object> weatherData = createDummyWeatherData(weekStart, weekEnd);
            System.out.println("天気データ作成完了: " + weatherData);
            return weatherData;
            
        } catch (Exception e) {
            System.err.println("天気データエラー: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }
    
    // ダミーデータ作成（テスト用）
    private Map<String, Object> createDummySalesData(LocalDate weekStart, LocalDate weekEnd) {
        System.out.println("ダミー売上データ作成開始: " + weekStart + " to " + weekEnd);
        
        List<String> dates = new ArrayList<>();
        List<Integer> paleAle = new ArrayList<>();
        List<Integer> ipa = new ArrayList<>();
        List<Integer> stout = new ArrayList<>();
        List<Integer> lager = new ArrayList<>();
        
        LocalDate currentDate = weekStart;
        Random random = new Random(weekStart.hashCode()); // 一貫性のためシード設定
        
        while (!currentDate.isAfter(weekEnd)) {
            dates.add(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("M/d")));
            
            // 現実的な売上データ生成（CSVデータに基づく）
            paleAle.add(random.nextInt(800) + 200);  // 200-1000本
            ipa.add(random.nextInt(600) + 150);      // 150-750本
            stout.add(random.nextInt(400) + 100);    // 100-500本
            lager.add(random.nextInt(1000) + 300);   // 300-1300本
            
            currentDate = currentDate.plusDays(1);
        }
        
        Map<String, Object> salesData = new HashMap<>();
        salesData.put("dates", dates);
        salesData.put("products", Map.of(
            "ペールエール", paleAle,
            "IPA", ipa,
            "スタウト", stout,
            "ラガー", lager
        ));
        
        System.out.println("作成された売上データ: " + salesData);
        return salesData;
    }
    
    // ダミー天気データ作成（テスト用）
    private Map<String, Object> createDummyWeatherData(LocalDate weekStart, LocalDate weekEnd) {
        System.out.println("ダミー天気データ作成開始: " + weekStart + " to " + weekEnd);
        
        List<String> dates = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        List<Integer> maxTemps = new ArrayList<>();
        List<Integer> minTemps = new ArrayList<>();
        List<Double> windSpeeds = new ArrayList<>();
        
        LocalDate currentDate = weekStart;
        Random random = new Random(weekStart.hashCode()); // 一貫性のためシード設定
        String[] weatherConditions = {"☀️", "☁️", "🌦️", "🌧️"};
        
        while (!currentDate.isAfter(weekEnd)) {
            dates.add(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("M/d")));
            conditions.add(weatherConditions[random.nextInt(weatherConditions.length)]);
            maxTemps.add(random.nextInt(15) + 15); // 15-30°C
            minTemps.add(random.nextInt(10) + 5);  // 5-15°C  
            windSpeeds.add(Math.round((random.nextDouble() * 8 + 1) * 10) / 10.0); // 1-9 m/s
            
            currentDate = currentDate.plusDays(1);
        }
        
        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("dates", dates);
        weatherData.put("conditions", conditions);
        weatherData.put("maxTemperatures", maxTemps);
        weatherData.put("minTemperatures", minTemps);
        weatherData.put("windSpeeds", windSpeeds);
        
        System.out.println("作成された天気データ: " + weatherData);
        return weatherData;
    }
    
    /* TODO: 後でSessionを有効化した際に使用
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
    */
}