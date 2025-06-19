package com.teama.javaproject.controller;

import com.teama.javaproject.entity.Product;
import com.teama.javaproject.entity.SalesRecord;
import com.teama.javaproject.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sales")
public class SalesController {
    
    @Autowired
    private SalesService salesService;
    
    // メイン画面表示
    @GetMapping("")
    public String showSalesInput(Model model) {
        // 商品一覧取得
        List<Product> products = salesService.getAllProducts();
        model.addAttribute("products", products);
        
        // 売上データ取得
        List<SalesRecord> salesRecords = salesService.getAllSalesRecords();
        model.addAttribute("salesRecords", salesRecords);
        
        // 日付別売上データをMap形式で取得
        Map<LocalDate, List<SalesRecord>> salesByDate = salesService.getSalesDataGroupedByDate();
        model.addAttribute("salesByDate", salesByDate);
        
        // 今日の日付をデフォルト値として設定
        model.addAttribute("defaultDate", LocalDate.now());
        
        return "sales_input";
    }
    
    // 売上データ一括登録
    @PostMapping("/register")
    public String registerSales(
            @RequestParam("salesDate") String salesDateStr,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        
        try {
            LocalDate salesDate = LocalDate.parse(salesDateStr);
            Map<Long, Integer> productSales = new HashMap<>();
            
            allParams.forEach((key, value) -> {
                if (key.startsWith("product_")) {
                    Long productId = Long.parseLong(key.substring(8));
                    Integer quantity = Integer.parseInt(value);
                    if (quantity > 0) {
                        productSales.put(productId, quantity);
                    }
                }
            });
            
            // サービス経由で登録
            salesService.registerSalesData(salesDate, productSales);
            redirectAttributes.addFlashAttribute("successMessage", "売上データを登録しました");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました: " + e.getMessage());
        }
        
        return "redirect:/sales";
    }
}