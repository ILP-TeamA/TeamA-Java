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

        // 如果没有传 date，默认今天
        if (predictionDate == null) {
            predictionDate = LocalDate.now();
        }

        List<Prediction> predictions = predictionService.getPredictionsByDate(predictionDate);

        model.addAttribute("predictions", predictions);
        model.addAttribute("predictionDate", predictionDate);

        return "prediction"; // templates/prediction.html
    }

    // 予測実行 (GET + POST 兼用)
    @RequestMapping(value = "/run", method = {RequestMethod.GET, RequestMethod.POST})
    public String runPrediction(@RequestParam("date") 
                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        predictionService.generatePrediction(date);

        // redirect → /prediction?predictionDate=xxxx-xx-xx
        return "redirect:/prediction?predictionDate=" + date;
    }

    // 確認画面 (POST)
    @PostMapping("/confirmation")
    public String showConfirmation(@RequestParam Map<String, String> paramMap,
                                   Model model) {

        // 1️⃣ DEBUG paramMap
        System.out.println("DEBUG paramMap = " + paramMap);

        // 2️⃣ 取出日期
        LocalDate date = LocalDate.parse(paramMap.get("date"));

        // 3️⃣ 取出本数入力
        List<Integer> quantities = new ArrayList<>();
        paramMap.forEach((key, value) -> {
            if (key.startsWith("quantities[")) {
                quantities.add(Integer.parseInt(value));
            }
        });

        // 4️⃣ 再查询当天的预测记录
        List<Prediction> predictions = predictionService.getPredictionsByDate(date);

        // 5️⃣ 填入 model
        model.addAttribute("predictions", predictions);
        model.addAttribute("quantities", quantities);
        model.addAttribute("date", date);

        return "confirmation"; // templates/confirmation.html
    }

}
