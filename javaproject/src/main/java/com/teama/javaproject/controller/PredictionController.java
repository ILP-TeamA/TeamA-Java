// package com.teama.javaproject.controller;

// import com.teama.javaproject.entity.Prediction;
// import com.teama.javaproject.entity.Product;
// import com.teama.javaproject.service.PredictionService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseBody;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Map;

// @Controller
// @RequestMapping("/prediction")
// public class PredictionController {
    
//     @Autowired
//     private PredictionService predictionService;
    
//     // 予測画面の表示
//     @GetMapping("")
//     public String showPredictionPage(Model model) {
//         // 今日の日付を取得
//         LocalDate today = LocalDate.now();
        
//         // 翌日の予測データを取得
//         Map<Product, Integer> predictions = predictionService.getPredictionsForDate(today.plusDays(1));
        
//         // モデルに予測データを追加
//         model.addAttribute("predictions", predictions);
//         model.addAttribute("targetDate", today.plusDays(1));
        
//         return "prediction";
//     }
    
//     // 特定日の予測データを取得するAPI
//     @GetMapping("/api/date/{date}")
//     @ResponseBody
//     public Map<Product, Integer> getPredictionForDate(@PathVariable String date) {
//         LocalDate targetDate = LocalDate.parse(date);
//         return predictionService.getPredictionsForDate(targetDate);
//     }
    
//     // 予測精度の表示
//     @GetMapping("/accuracy")
//     public String showPredictionAccuracy(Model model) {
//         // 過去30日間の予測精度を取得
//         List<Prediction> accuracyData = predictionService.getAccuracyForLast30Days();
        
//         // モデルにデータを追加
//         model.addAttribute("accuracyData", accuracyData);
        
//         return "prediction-accuracy";
//     }
// }
