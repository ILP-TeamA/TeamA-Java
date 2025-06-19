package com.teama.javaproject.controller;

import com.teama.javaproject.entity.Prediction;
import com.teama.javaproject.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/prediction")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    // GET → 画面表示
    @GetMapping
    public String showPredictions(Model model) {
        List<Prediction> predictions = predictionService.getTodayPredictions();
        model.addAttribute("predictions", predictions);
        return "prediction";
    }

    // POST → 予測実行
    @PostMapping("/run")
    public String runPrediction(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        predictionService.generatePrediction(date);
        return "redirect:/prediction";
    }
}
