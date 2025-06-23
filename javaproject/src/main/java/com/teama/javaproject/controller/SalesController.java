package com.teama.javaproject.controller;

import com.teama.javaproject.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;

    /**
     * 売上入力画面を表示
     */
    @GetMapping
    public String salesPage(Model model) {
        System.out.println("=== salesPage メソッドが呼び出されました ===");
        return "sales_input";
    }

    /**
     * 売上データ登録処理
     */
    @PostMapping("/register")
    public String registerSales(
            @RequestParam("salesDate") String salesDate,
            @RequestParam("createBy") Integer createBy,
            @RequestParam("salesId") Integer salesId,
            @RequestParam("product_1") Integer product1,
            @RequestParam("product_2") Integer product2,
            @RequestParam("product_3") Integer product3,
            @RequestParam("product_4") Integer product4,
            @RequestParam("product_5") Integer product5,
            @RequestParam("product_6") Integer product6,
            RedirectAttributes redirectAttributes) {

        try {
            System.out.println("=== 売上データ登録開始 ===");
            System.out.println("販売日: " + salesDate);
            System.out.println("登録者ID: " + createBy);
            System.out.println("売上ID: " + salesId);

            // Map<Long, Integer>形式でプロダクト販売データを作成
            Map<Long, Integer> productSales = new HashMap<>();
            productSales.put(1L, product1);  // product_id = 1 (ホワイトビール)
            productSales.put(2L, product2);  // product_id = 2 (ラガー)
            productSales.put(3L, product3);  // product_id = 3 (ペールエール)
            productSales.put(4L, product4);  // product_id = 4 (フルーツビール)
            productSales.put(5L, product5);  // product_id = 5 (黒ビール)
            productSales.put(6L, product6);  // product_id = 6 (IPA)

            // 売上データを登録
            salesService.registerSalesData(salesDate, salesId, productSales, createBy);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "売上データが正常に登録されました！");

        } catch (Exception e) {
            System.err.println("売上データ登録エラー: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                "システムエラーが発生しました: " + e.getMessage());
        }

        return "redirect:/sales";
    }

    /**
     * 売上履歴一覧画面を表示
     */
    @GetMapping("/history")
    public String salesHistory(
            @RequestParam(value = "searchDate", required = false) String searchDate,
            Model model) {
        try {
            List<Map<String, Object>> salesHistory;
            
            if (searchDate != null && !searchDate.isEmpty()) {
                // 日付指定検索
                salesHistory = salesService.getSalesHistoryByDate(searchDate);
                model.addAttribute("searchDate", searchDate);
                System.out.println("日付指定検索: " + searchDate + ", 結果件数: " + salesHistory.size());
            } else {
                // 全履歴取得
                salesHistory = salesService.getSalesHistory();
                System.out.println("全履歴取得: 結果件数: " + salesHistory.size());
            }
            
            // 曜日を日本語に統一
            for (Map<String, Object> sales : salesHistory) {
                String dayOfWeek = (String) sales.get("day_of_week");
                if (dayOfWeek != null) {
                    String convertedDayOfWeek = convertToJapaneseDayOfWeek(dayOfWeek);
                    sales.put("day_of_week", convertedDayOfWeek);
                }
            }
            
            model.addAttribute("salesHistory", salesHistory);
            return "sales_history";
        } catch (Exception e) {
            System.err.println("売上履歴取得エラー: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "売上履歴の取得に失敗しました: " + e.getMessage());
            return "sales_history";
        }
    }
    
    /**
     * 曜日を日本語に変換
     */
    private String convertToJapaneseDayOfWeek(String dayOfWeek) {
        if (dayOfWeek == null) return "";
        
        // 英語の曜日を日本語に変換
        switch (dayOfWeek.toLowerCase().trim()) {
            case "monday": return "月曜日";
            case "tuesday": return "火曜日"; 
            case "wednesday": return "水曜日";
            case "thursday": return "木曜日";
            case "friday": return "金曜日";
            case "saturday": return "土曜日";
            case "sunday": return "日曜日";
            default: 
                // 既に日本語の場合はそのまま返す
                return dayOfWeek;
        }
    }

    /**
     * 特定日の売上データをJSON形式で取得（API用）
     */
    @GetMapping("/check/{salesId}")
    @ResponseBody
    public ResponseEntity<String> getSalesData(@PathVariable Integer salesId) {
        try {
            // sales_idが存在するかチェック
            boolean exists = salesService.existsBySalesId(salesId);
            if (exists) {
                return ResponseEntity.ok("Sales ID: " + salesId + " のデータが存在します");
            } else {
                return ResponseEntity.ok("Sales ID: " + salesId + " のデータは存在しません");
            }
        } catch (Exception e) {
            System.err.println("売上データ取得エラー: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}