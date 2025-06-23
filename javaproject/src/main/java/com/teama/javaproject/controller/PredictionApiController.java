package com.teama.javaproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 需要予測API
 */
@RestController
@RequestMapping("/api/prediction")
public class PredictionApiController {

    private static final String PREDICTION_API_URL = "https://predictor-teama.azurewebsites.net/api/predictor";

    @GetMapping("/demand")
    public ResponseEntity<List<Map<String, Object>>> getDemandPrediction(@RequestParam String date) {
        try {
            // 外部予測APIを呼び出し
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = PREDICTION_API_URL + "?date=" + date;
            
            System.out.println("予測API呼び出し: " + apiUrl);
            
            // 外部APIから予測データを取得
            String response = restTemplate.getForObject(apiUrl, String.class);
            System.out.println("予測API応答: " + response);
            
            // レスポンスを解析して商品別予測データを作成
            List<Map<String, Object>> predictions = parsePredictionResponse(response);
            
            return ResponseEntity.ok(predictions);
            
        } catch (Exception e) {
            System.err.println("予測API呼び出しエラー: " + e.getMessage());
            e.printStackTrace();
            
            // エラー時はダミーデータを返す
            List<Map<String, Object>> fallbackData = createFallbackPredictions();
            return ResponseEntity.ok(fallbackData);
        }
    }
    
    /**
     * 予測APIのレスポンスを解析
     */
    private List<Map<String, Object>> parsePredictionResponse(String response) {
        List<Map<String, Object>> predictions = new ArrayList<>();
        
        try {
            // 簡単なパース（実際のAPIレスポンス形式に応じて調整が必要）
            if (response != null && !response.trim().isEmpty()) {
                // JSONパースまたは文字列解析
                // ここでは仮の実装
                predictions = createSamplePredictions();
            } else {
                predictions = createFallbackPredictions();
            }
        } catch (Exception e) {
            System.err.println("予測レスポンス解析エラー: " + e.getMessage());
            predictions = createFallbackPredictions();
        }
        
        return predictions;
    }
    
    /**
     * サンプル予測データ作成
     */
    private List<Map<String, Object>> createSamplePredictions() {
        List<Map<String, Object>> predictions = new ArrayList<>();
        
        predictions.add(createPrediction(1, "ホワイトビール", 85));
        predictions.add(createPrediction(2, "ラガー", 120));
        predictions.add(createPrediction(3, "ペールエール", 95));
        predictions.add(createPrediction(4, "フルーツビール", 60));
        predictions.add(createPrediction(5, "黒ビール", 45));
        predictions.add(createPrediction(6, "IPA", 75));
        
        return predictions;
    }
    
    /**
     * フォールバック用予測データ
     */
    private List<Map<String, Object>> createFallbackPredictions() {
        List<Map<String, Object>> predictions = new ArrayList<>();
        
        predictions.add(createPrediction(1, "ホワイトビール", 50));
        predictions.add(createPrediction(2, "ラガー", 80));
        predictions.add(createPrediction(3, "ペールエール", 65));
        predictions.add(createPrediction(4, "フルーツビール", 35));
        predictions.add(createPrediction(5, "黒ビール", 25));
        predictions.add(createPrediction(6, "IPA", 45));
        
        return predictions;
    }
    
    /**
     * 予測データオブジェクト作成
     */
    private Map<String, Object> createPrediction(int productId, String productName, int quantity) {
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("productId", productId);
        prediction.put("productName", productName);
        prediction.put("predictedQuantity", quantity);
        return prediction;
    }
}