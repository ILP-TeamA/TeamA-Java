package com.teama.javaproject.service;

import com.teama.javaproject.entity.*;
import com.teama.javaproject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PredictionService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private DailyBeerSalesRepository salesRepo;

    @Autowired
    private WeatherHistoryRepository weatherRepo;

    @Autowired
    private PredictionRepository predictionRepo;

    // 予測を生成するメソッド
    public List<Prediction> generatePrediction(LocalDate date) {
        List<Product> products = productRepo.findAll();
        List<Prediction> result = new ArrayList<>();

        for (Product product : products) {
            // 仮ロジック：固定数量（例：10）にする
            // → 本当は salesRepo や weatherRepo から過去データ取って計算する
            int predictedQty = 10;

            Prediction p = new Prediction();
            p.setPredictionDate(date);
            p.setProduct(product);
            p.setPredictedQuantity(predictedQty);

            result.add(predictionRepo.save(p));
        }

        return result;
    }

    // 今日の予測を取得
    public List<Prediction> getTodayPredictions() {
        LocalDate today = LocalDate.now();
        return predictionRepo.findByPredictionDate(today);
    }
}
