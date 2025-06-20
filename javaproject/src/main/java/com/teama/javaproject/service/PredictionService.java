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

        // 1️⃣ 调用 Python API
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 准备请求 body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("prediction_date", date.toString());

            // 设置 header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 包装请求
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // 发请求
            ResponseEntity<String> response = restTemplate.postForEntity(PYTHON_API_URL, request, String.class);

            // 解析返回
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());

                // 提取 beer_sales_predictions 里的数据
                JsonNode beerSales = root.path("beer_sales_predictions");

                List<Product> products = productRepo.findAll();
                List<Prediction> result = new ArrayList<>();

                for (Product p : products) {
                    // 根据产品名去找预测值
                    String productName = p.getName();

                    double predictedQtyDouble = beerSales.path(productName).asDouble(0);
                    int predictedQty = (int)Math.round(predictedQtyDouble);  // 转换成 int，四舍五入

                    Prediction prediction = new Prediction();
                    prediction.setPredictionDate(date);
                    prediction.setProduct(p);
                    prediction.setPredictedQuantity(predictedQty);

                    result.add(predictionRepo.save(prediction));
                }

                return result;

            } else {
                throw new RuntimeException("Python API 调用失败，状态码: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("调用 Python API 出错: " + e.getMessage(), e);
        }
    }

    // 指定日期の予測取得
    public List<Prediction> getPredictionsByDate(LocalDate date) {
        return predictionRepo.findByPredictionDate(date);
    }

}

