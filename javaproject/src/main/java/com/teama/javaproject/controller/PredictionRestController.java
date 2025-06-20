package com.teama.javaproject.controller;

import com.teama.javaproject.entity.Prediction;
import com.teama.javaproject.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prediction")
public class PredictionRestController {

    @Autowired
    private PredictionService predictionService;

    // POST /api/prediction/run
    @PostMapping("/run")
    public List<Prediction> runPrediction(@RequestBody PredictionRequest request) {
        LocalDate date = request.getDate();
        return predictionService.generatePrediction(date);
    }

    // DTOクラス（リクエストBody用）
    public static class PredictionRequest {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate date;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    }
}
