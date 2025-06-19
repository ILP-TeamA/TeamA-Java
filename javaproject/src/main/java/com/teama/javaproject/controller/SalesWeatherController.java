package com.teama.javaproject.controller;

import com.teama.javaproject.entity.DailyBeerSales;
import com.teama.javaproject.entity.WeatherHistory;
import com.teama.javaproject.service.SalesWeatherService;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class SalesWeatherController {

    // TODO: データベース接続後に@Autowiredを使用
    // @Autowired
    private SalesWeatherService salesWeatherService;
    
    // 一時的なコンストラクタ - データベース接続後は削除
    public SalesWeatherController() {
        this.salesWeatherService = new SalesWeatherService();
    }

    /**
     * 販売実績・天気分析ページ表示（初期表示）
     */
    @GetMapping("/sales-weather-analysis")
    public String showSalesWeatherAnalysis(Model model) {
        // デフォルトで今日から1週間前の日付を設定
        LocalDate defaultDate = LocalDate.now().minusDays(7);
        return loadSalesWeatherData(defaultDate, model);
    }

    /**
     * 日付選択後の販売実績・天気分析ページ表示
     */
    @PostMapping("/sales-weather-analysis")
    public String showSalesWeatherAnalysisWithDate(
            @RequestParam("selectedDate") String selectedDateStr,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            LocalDate selectedDate = LocalDate.parse(selectedDateStr);
            
            // 未来の日付チェック
            if (selectedDate.isAfter(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("errorMessage", "その日付は選択できません。");
                return "redirect:/sales-weather-analysis";
            }
            
            return loadSalesWeatherData(selectedDate, model);
            
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "正しい日付を入力してください。");
            return "redirect:/sales-weather-analysis";
        }
    }

    /**
     * 販売実績と天気データを読み込んでモデルに設定
     */
    private String loadSalesWeatherData(LocalDate startDate, Model model) {
        try {
            // 一週間分の販売実績データを取得
            List<DailyBeerSales> salesData = salesWeatherService.getWeeklySalesData(startDate);
            
            // 一週間分の天気データを取得
            List<WeatherHistory> weatherData = salesWeatherService.getWeeklyWeatherData(startDate);
            
            // Chart.js用のデータを準備
            String chartLabels = salesWeatherService.generateChartLabels(startDate);
            String chartData = salesWeatherService.generateChartData(salesData);
            
            // モデルに追加
            model.addAttribute("salesData", salesData);
            model.addAttribute("weatherData", weatherData);
            model.addAttribute("chartLabels", chartLabels);
            model.addAttribute("chartData", chartData);
            model.addAttribute("selectedDate", startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            model.addAttribute("displayPeriod", 
                startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + " ~ " + 
                startDate.plusDays(6).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "データの取得中にエラーが発生しました。");
            // エラー時は空のデータを設定
            model.addAttribute("selectedDate", startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        return "sales-weather-analysis";
    }

    /**
     * 発注量予測ページへリダイレクト
     */
    @GetMapping("/order-quantity-prediction")
    public String redirectToOrderQuantityPrediction() {
        // TODO: 発注量予測ページの実装後にURLを修正
        return "redirect:/order-quantity-prediction";
    }

    /**
     * ログアウト処理
     */
    @PostMapping("/logout")
    public String logout() {
        // TODO: セッション削除処理を実装
        return "redirect:/login";
    }
}