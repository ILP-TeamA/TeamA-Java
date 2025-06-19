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
            
            // 未来の日付チェック
            if (salesDate.isAfter(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("errorMessage", "未来の日付は登録できません。");
                return "redirect:/sales";
            }
            
            // 商品別売上データを抽出
            Map<Long, Integer> productSales = new HashMap<>();
            int totalQuantity = 0;
            
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("product_")) {
                    Long productId = Long.valueOf(key.substring(8)); // "product_"を除去
                    String quantityStr = entry.getValue();
                    if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                        Integer quantity = Integer.valueOf(quantityStr);
                        productSales.put(productId, quantity);
                        totalQuantity += quantity;
                    }
                }
            }
            
            // 最低1本の売上が必要
            if (totalQuantity == 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "少なくとも1つの商品の売上本数を入力してください。");
                return "redirect:/sales";
            }
            
            // サービス経由で登録
            salesService.registerSalesData(salesDate, productSales);
            
            // 合計金額計算
            int totalRevenue = calculateTotalRevenue(productSales);
            
            redirectAttributes.addFlashAttribute("successMessage",
                String.format("売上データを正常に登録しました！（合計%d本・¥%,d）", totalQuantity, totalRevenue));
            
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "数値の形式が正しくありません。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました: " + e.getMessage());
        }
        
        return "redirect:/sales";
    }
    
    private int calculateTotalRevenue(Map<Long, Integer> productSales) {
        int total = 0;
        List<Product> products = salesService.getAllProducts();
        
        for (Product product : products) {
            Integer quantity = productSales.get(product.getId());
            if (quantity != null && quantity > 0) {
                total += product.getPrice() * quantity;
            }
        }
        
        return total;
    }
}