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
public String showSalesInput(
    @RequestParam(value = "salesId", required = false) Integer salesId,
    Model model) {
    // 商品一覧取得
    List<Product> products = salesService.getAllProducts();
    model.addAttribute("products", products);

    // 入力欄に表示する販売データ
    Map<Long, Integer> salesMap = new HashMap<>();
    if (salesId != null) {
        List<SalesRecord> records = salesService.getSalesRecordsBySalesId(salesId);
        for (SalesRecord record : records) {
            salesMap.put(record.getProduct().getId().longValue(), record.getQuantity());
        }
    }
    model.addAttribute("salesId", salesId);
    model.addAttribute("salesMap", salesMap);

    // 販売IDリスト（プルダウン用）
    List<Integer> salesIdList = salesService.getAllSalesIds();
    model.addAttribute("salesIdList", salesIdList);


    return "sales_input";
}

    // @GetMapping("")
    // public String showSalesInput(Model model) {
    //     // 商品一覧取得
    //     List<Product> products = salesService.getAllProducts();
    //     model.addAttribute("products", products);
        
    //     // 売上データ取得
    //     List<SalesRecord> salesRecords = salesService.getAllSalesRecords();
    //     model.addAttribute("salesRecords", salesRecords);
        
    //     // 販売ID別売上データをMap形式で取得
    //     Map<Integer, List<SalesRecord>> salesBySalesId = salesService.getSalesDataGroupedBySalesId();
    //     model.addAttribute("salesBySalesId", salesBySalesId);
        
    //     return "sales_input";
    // }
    
    // 売上データ一括登録
    @PostMapping("/register")
    public String registerSales(
            @RequestParam("salesId") Integer salesId,
            @RequestParam Map<String, String> allParams,
            @RequestParam("createBy") Integer createBy,
            RedirectAttributes redirectAttributes) {

        try {
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

            salesService.registerSalesData(salesId, productSales, createBy);
            redirectAttributes.addFlashAttribute("successMessage", "売上データを登録しました");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました: " + e.getMessage());
        }

        return "redirect:/sales";
    }
    
    // 特定の販売IDの売上データを取得
    @GetMapping("/details/{salesId}")
    public String getSalesDetails(@PathVariable Integer salesId, Model model) {
        List<SalesRecord> salesRecords = salesService.getSalesRecordsBySalesId(salesId);
        model.addAttribute("salesRecords", salesRecords);
        model.addAttribute("salesId", salesId);
        return "sales_details";
    }
    
    // 販売IDの重複チェック用API
    @GetMapping("/check/{salesId}")
    @ResponseBody
    public Map<String, Boolean> checkSalesIdExists(@PathVariable Integer salesId) {
        boolean exists = salesService.hasSalesDataForSalesId(salesId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }

    // 履歴一覧ページ表示
    @GetMapping("/history")
    public String showSalesHistory(
            @RequestParam(value = "date", required = false) String dateStr,
            Model model) {
        LocalDate date;
        if (dateStr == null || dateStr.isEmpty()) {
            date = LocalDate.now();
        } else {
            date = LocalDate.parse(dateStr);
        }
        // 指定日付の売上レコード取得
        List<SalesRecord> salesList = salesService.getSalesRecordsByDate(date);
        // 総売上額計算
        int totalAmount = salesList.stream()
                .mapToInt(SalesRecord::getRevenue) // getAmount()がなければ数量×単価で計算
                .sum();
        model.addAttribute("selectedDate", date);
        model.addAttribute("salesList", salesList);
        model.addAttribute("totalAmount", totalAmount);
        return "sales_history";
    }

    // 編集画面表示
    @GetMapping("/edit/{salesId}")
public String editSales(@PathVariable Integer salesId, Model model) {
    List<Product> products = salesService.getAllProducts();
    List<SalesRecord> records = salesService.getSalesRecordsBySalesId(salesId);
    Map<Long, Integer> salesMap = new HashMap<>();
    for (SalesRecord record : records) {
        salesMap.put(record.getProduct().getId().longValue(), record.getQuantity());
    }
    model.addAttribute("products", products);
    model.addAttribute("salesId", salesId);
    model.addAttribute("salesMap", salesMap);
    return "sales_edit";
}

   // 編集内容保存
@PostMapping("/edit")
public String updateSales(
        @RequestParam("salesId") Integer salesId,
        @RequestParam Map<String, String> allParams,
        @RequestParam("createBy") Integer createBy,
        RedirectAttributes redirectAttributes) {
    try {
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
        salesService.updateSalesData(salesId, productSales, createBy);
        redirectAttributes.addFlashAttribute("successMessage", "売上データを更新しました");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました: " + e.getMessage());
    }
    return "redirect:/sales/history";
}

}