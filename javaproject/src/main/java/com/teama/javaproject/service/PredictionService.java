package com.teama.javaproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teama.javaproject.entity.Prediction;
import com.teama.javaproject.entity.Product;
import com.teama.javaproject.repository.PredictionRepository;
import com.teama.javaproject.repository.ProductRepository;

import java.time.LocalDate;
import java.util.*;

@Service
public class PredictionService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private PredictionRepository predictionRepo;

    private final String PYTHON_API_URL = "https://predictor-teama.azurewebsites.net/api/predictor";

    // 予測実行
    public List<Prediction> generatePrediction(LocalDate date) {
        System.out.println("=== 予測実行開始 ===");
        System.out.println("予測日付: " + date);

        // 1️⃣ 既存の予測データを完全削除（重複を防ぐため）
        List<Prediction> existingPredictions = predictionRepo.findByPredictionDate(date);
        if (!existingPredictions.isEmpty()) {
            System.out.println("🗑️ 既存の予測データを削除中: " + existingPredictions.size() + "件");
            for (Prediction p : existingPredictions) {
                System.out.println("  削除対象: " + p.getProduct().getName() + " = " + p.getPredictedQuantity());
            }
            predictionRepo.deleteAll(existingPredictions);
            predictionRepo.flush(); // 即座に削除を実行
            System.out.println("✅ 削除完了");
        } else {
            System.out.println("📭 既存データなし");
        }

        // 2️⃣ Python APIを呼び出し
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 準備：リクエストボディ（正しいパラメータ名に修正）
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("target_date", date.toString()); // ✅ 正しいパラメータ名

            System.out.println("🗓️ APIに送信する日付: " + date.toString());
            System.out.println("📝 リクエストボディ: " + requestBody);
            System.out.println("🔗 API URL: " + PYTHON_API_URL);

            // 準備：ヘッダー
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // リクエスト実行
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            System.out.println("Python API呼び出し中...");

            ResponseEntity<String> response = restTemplate.postForEntity(PYTHON_API_URL, request, String.class);

            // 3️⃣ レスポンス解析
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Python APIからのレスポンス受信成功");
                String responseBody = response.getBody();
                System.out.println("🔍 APIレスポンス全体: " + responseBody);

                JsonNode root = objectMapper.readTree(responseBody);

                // 🔥 修正箇所：正しいキー名を使用
                JsonNode beerRecommendations = root.path("beer_purchase_recommendations");
                System.out.println("beer_purchase_recommendations取得: " + beerRecommendations.toString());

                // デバッグ：APIレスポンスの全キーを表示
                System.out.println("=== APIレスポンス内の商品キー一覧 ===");
                beerRecommendations.fieldNames().forEachRemaining(fieldName -> System.out
                        .println("APIキー: [" + fieldName + "] = " + beerRecommendations.get(fieldName).asDouble()));

                List<Product> products = productRepo.findAll();

                // デバッグ：データベース商品名一覧を表示
                System.out.println("=== データベース商品名一覧 ===");
                for (Product p : products) {
                    System.out.println("DB商品名: [" + p.getName() + "] ID=" + p.getId());
                }

                List<Prediction> results = new ArrayList<>();

                // 4️⃣ 商品ごとに予測データを作成
                for (Product product : products) {
                    String productName = product.getName();

                    // APIから予測値を取得（繰り上げで整数化）
                    JsonNode productNode = beerRecommendations.path(productName);
                    double predictedQtyDouble = productNode.asDouble(0);
                    int predictedQty = (int) Math.ceil(predictedQtyDouble);

                    System.out.println("🔍 商品名マッチング: [" + productName + "]");
                    System.out.println("  ➤ APIノード存在: " + !productNode.isMissingNode());
                    System.out.println("  ➤ API生値: " + predictedQtyDouble);
                    System.out.println("  ➤ 繰り上げ後: " + predictedQty);

                    if (predictedQtyDouble == 0) {
                        System.err.println("⚠️ 警告: " + productName + " の予測値が0です！商品名が一致していない可能性があります。");
                    }

                    System.out.println("📦 " + productName + ": API値=" + predictedQtyDouble + " → 保存値=" + predictedQty);

                    // 予測エンティティを作成
                    Prediction prediction = new Prediction();
                    prediction.setPredictionDate(date);
                    prediction.setProduct(product);
                    prediction.setPredictedQuantity(predictedQty);

                    // データベースに保存
                    Prediction savedPrediction = predictionRepo.save(prediction);
                    results.add(savedPrediction);

                    System.out.println(
                            "💾 保存完了: ID=" + savedPrediction.getId() + ", 値=" + savedPrediction.getPredictedQuantity());
                }

                System.out.println("=== 予測実行完了 ===");
                return results;

            } else {
                throw new RuntimeException("Python API呼び出し失敗。ステータスコード: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Python API呼び出しエラー: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Python API呼び出しエラー: " + e.getMessage(), e);
        }
    }

    // 指定日付の予測取得
    public List<Prediction> getPredictionsByDate(LocalDate date) {
        System.out.println("予測データ取得: " + date);
        List<Prediction> predictions = predictionRepo.findByPredictionDate(date);
        System.out.println("取得件数: " + predictions.size());

        // デバッグ：取得した予測データを出力
        for (Prediction p : predictions) {
            System.out.println("取得データ: " + p.getProduct().getName() + " = " + p.getPredictedQuantity());
        }

        return predictions;
    }
}