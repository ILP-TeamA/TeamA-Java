package com.teama.javaproject.controller;

import com.teama.javaproject.entity.WeatherHistory;
import com.teama.javaproject.service.SalesWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 販売実績・天気分析コントローラー
 */
@Controller
public class SalesWeatherController {

    @Autowired
    private SalesWeatherService salesWeatherService;

    /**
     * 初期表示（GET）
     */
    @GetMapping("/sales-weather-analysis")
    public String showSalesWeatherAnalysis(Model model) {
        // デフォルトで最新のデータがある日付を設定
        Optional<LocalDate> latestDate = salesWeatherService.getLatestWeatherDataDate();
        LocalDate defaultDate = latestDate.orElse(LocalDate.of(2025, 6, 12)); // 2025年6月12日をデフォルト
        
        return loadSalesWeatherData(defaultDate, model);
    }

    /**
     * 日付選択後の表示（POST）
     */
    @PostMapping("/sales-weather-analysis")
    public String showSalesWeatherAnalysisWithDate(@RequestParam("selectedDate") String selectedDateStr, Model model) {
        try {
            LocalDate selectedDate = LocalDate.parse(selectedDateStr);
            return loadSalesWeatherData(selectedDate, model);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "日付の解析に失敗しました。正しい日付を入力してください。");
            // エラー時はデフォルト日付でデータを読み込み
            LocalDate defaultDate = LocalDate.of(2025, 6, 12);
            return loadSalesWeatherData(defaultDate, model);
        }
    }

    /**
     * データ読み込み共通処理
     */
    private String loadSalesWeatherData(LocalDate startDate, Model model) {
        try {
            // 利用可能な最新日付をチェック
            Optional<LocalDate> latestAvailableDate = salesWeatherService.getLatestWeatherDataDate();
            
            // 選択日付が利用可能データ範囲を超えている場合の調整
            if (latestAvailableDate.isPresent() && startDate.isAfter(latestAvailableDate.get())) {
                startDate = latestAvailableDate.get().minusDays(6); // 1週間分確保
                model.addAttribute("errorMessage", 
                    "選択された日付のデータが存在しません。利用可能な最新データを表示します。（データは" + 
                    latestAvailableDate.get().format(DateTimeFormatter.ofPattern("yyyy年M月d日")) + "まで）");
            }
            
            // 未来の日付が選択された場合
            if (startDate.isAfter(LocalDate.now())) {
                startDate = LocalDate.now().minusDays(6);
                model.addAttribute("errorMessage", "未来の日付は選択できません。直近のデータを表示します。");
            }
            
            // 天気データを取得
            List<WeatherHistory> weatherData = salesWeatherService.getWeeklyWeatherData(startDate);
            
            // 販売データを取得
            Map<String, List<Integer>> salesData = salesWeatherService.getWeeklySalesData(startDate);
            
            // Chart.js用のラベルを生成
            List<String> chartLabels = salesWeatherService.generateChartLabels(startDate);
            
            // Chart.js用のデータセットを生成
            String chartDatasets = salesWeatherService.generateChartDatasets(salesData);
            
            // 表示期間を生成
            String displayPeriod = salesWeatherService.generateDisplayPeriod(startDate);
            
            // モデルに属性を設定
            model.addAttribute("weatherData", weatherData);
            model.addAttribute("chartLabels", chartLabels);
            model.addAttribute("chartData", chartDatasets);
            model.addAttribute("displayPeriod", displayPeriod);
            model.addAttribute("selectedDate", startDate.toString());
            
            // データが存在しない場合の警告
            if (weatherData.isEmpty()) {
                model.addAttribute("errorMessage", 
                    "選択された期間の天気データが見つかりません。サンプルデータを表示しています。");
            }
            
            // 成功メッセージがある場合
            if (weatherData.size() == 7) {
                model.addAttribute("successMessage", 
                    "データを正常に読み込みました。（" + displayPeriod + "）");
            }
            
        } catch (Exception e) {
            // エラー処理
            model.addAttribute("errorMessage", 
                "データの取得中にエラーが発生しました: " + e.getMessage());
            
            // エラー時のデフォルトデータ設定
            setDefaultModelAttributes(model, startDate);
        }
        
        return "sales-weather-analysis";
    }
    
    /**
     * エラー時のデフォルトモデル属性設定
     */
    private void setDefaultModelAttributes(Model model, LocalDate startDate) {
        // デフォルトのChart.jsラベル
        List<String> defaultLabels = salesWeatherService.generateChartLabels(startDate);
        
        // デフォルトの販売データ（サンプル）
        Map<String, List<Integer>> defaultSalesData = Map.of(
            "ホワイトビール", List.of(120, 150, 180, 200, 170, 160, 190),
            "ラガー", List.of(100, 130, 160, 180, 150, 140, 170),
            "ペールエール", List.of(80, 110, 140, 160, 130, 120, 150),
            "フルーツビール", List.of(60, 90, 120, 140, 110, 100, 130),
            "黒ビール", List.of(40, 70, 100, 120, 90, 80, 110),
            "IPA", List.of(90, 120, 150, 170, 140, 130, 160)
        );
        
        String defaultChartDatasets = salesWeatherService.generateChartDatasets(defaultSalesData);
        String displayPeriod = salesWeatherService.generateDisplayPeriod(startDate);
        
        model.addAttribute("chartLabels", defaultLabels);
        model.addAttribute("chartData", defaultChartDatasets);
        model.addAttribute("displayPeriod", displayPeriod);
        model.addAttribute("selectedDate", startDate.toString());
        model.addAttribute("weatherData", null); // サンプルデータはHTMLで表示
    }
    
    /**
     * ログアウト処理
     */
    @PostMapping("/logout")
    public String logout() {
        // セッションクリア等の処理
        return "redirect:/login";
    }
    
    /**
     * 発注量予測ページへのリダイレクト
     */
    @GetMapping("/order-quantity-prediction")
    public String redirectToOrderPrediction() {
        // 発注量予測ページにリダイレクト
        return "order-quantity-prediction";
    }
}