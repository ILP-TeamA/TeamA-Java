package com.teama.javaproject.controller;

import com.teama.javaproject.entity.Prediction;
import com.teama.javaproject.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/prediction")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    // 画面表示 (GET)
    @GetMapping
    public String showPredictions(@RequestParam(value = "predictionDate", required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate predictionDate,
                                  Model model) {

        System.out.println("=== 予測画面表示 ===");
        
        // デフォルト日付設定
        if (predictionDate == null) {
            predictionDate = LocalDate.now();
        }
        
        System.out.println("表示対象日付: " + predictionDate);

        // 予測データを取得
        List<Prediction> predictions = predictionService.getPredictionsByDate(predictionDate);
        
        // モデルに追加
        model.addAttribute("predictions", predictions);
        model.addAttribute("predictionDate", predictionDate);

        System.out.println("画面に表示する予測データ件数: " + predictions.size());
        
        return "prediction"; // templates/prediction.html
    }

    // 予測実行 (POST)
    @PostMapping("/run")
    public String runPrediction(@RequestParam("date") 
                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        System.out.println("=== 予測実行リクエスト ===");
        System.out.println("リクエスト日付: " + date);

        try {
            // 予測を実行
            List<Prediction> predictions = predictionService.generatePrediction(date);
            System.out.println("予測実行成功。生成件数: " + predictions.size());
            
        } catch (Exception e) {
            System.err.println("予測実行エラー: " + e.getMessage());
            // エラーが発生してもリダイレクトして、エラーメッセージは画面で表示
        }

        // 予測結果画面にリダイレクト
        return "redirect:/prediction?predictionDate=" + date;
    }

    // 確認画面 (POST)
    @PostMapping("/confirmation")
    public String showConfirmation(@RequestParam Map<String, String> paramMap,
                                   Model model) {

        System.out.println("=== 確認画面遷移 ===");
        System.out.println("受信パラメータ: " + paramMap);

        try {
            // 1️⃣ 日付を取得
            String dateStr = paramMap.get("date");
            if (dateStr == null || dateStr.isEmpty()) {
                throw new IllegalArgumentException("日付パラメータが見つかりません");
            }
            LocalDate date = LocalDate.parse(dateStr);
            System.out.println("確認画面の日付: " + date);

            // 2️⃣ 数量入力を取得
            List<Integer> quantities = new ArrayList<>();
            
            // パラメータから quantities[0], quantities[1], ... を抽出
            int index = 0;
            while (true) {
                String quantityKey = "quantities[" + index + "]";
                String quantityValue = paramMap.get(quantityKey);
                
                if (quantityValue == null) {
                    break; // もうないので終了
                }
                
                try {
                    int quantity = Integer.parseInt(quantityValue);
                    quantities.add(quantity);
                    System.out.println("数量[" + index + "]: " + quantity);
                } catch (NumberFormatException e) {
                    quantities.add(0); // パースエラーの場合は0
                    System.out.println("数量[" + index + "]: パースエラー、0に設定");
                }
                
                index++;
            }

            System.out.println("合計入力数量件数: " + quantities.size());

            // 3️⃣ 予測データを再取得
            List<Prediction> predictions = predictionService.getPredictionsByDate(date);
            System.out.println("確認画面用予測データ件数: " + predictions.size());

            // 4️⃣ モデルに設定
            model.addAttribute("predictions", predictions);
            model.addAttribute("quantities", quantities);
            model.addAttribute("date", date);

            return "confirmation"; // templates/confirmation.html

        } catch (Exception e) {
            System.err.println("確認画面エラー: " + e.getMessage());
            e.printStackTrace();
            
            // エラー時は予測画面に戻る
            return "redirect:/prediction";
        }
    }
}