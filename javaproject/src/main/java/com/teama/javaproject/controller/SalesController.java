package com.teama.javaproject.controller;

import org.springframework.beans.factory.annotation.Autowired; 

import org.springframework.stereotype.Controller; 

import org.springframework.ui.Model; 

import org.springframework.web.bind.annotation.*; 

import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import java.util.ArrayList; 

import java.util.HashMap; 

import java.util.List; 

import java.util.Map; 

 

@Controller 

@RequestMapping("/sales") 

public class SalesController { 

 

    // @Autowired 

    // private SalesService salesService;  // 一時的にコメントアウト 

 

    @GetMapping 

    public String salesPage(Model model) { 

        return "sales_input";  // sales.htmlを参照（ファイル名に合わせて修正） 

    } 

 

    @PostMapping("/register") 

    public String registerSales( 

            @RequestParam("salesDate") String salesDate, 

            @RequestParam("salesId") Integer salesId, 

            @RequestParam Map<String, String> allParams, 

            @RequestParam("createBy") Integer createBy, 

            RedirectAttributes redirectAttributes) { 

 

        try { 

            System.out.println("=== 売上登録開始 ==="); 

            System.out.println("販売日付: " + salesDate); 

            System.out.println("販売ID: " + salesId); 

            System.out.println("登録者ID: " + createBy); 

            System.out.println("受信パラメータ: " + allParams); 

 

            // 一時的にサービス呼び出しをコメントアウト 

            // salesService.registerSalesData(salesDate, salesId, productSales, createBy); 

             

            redirectAttributes.addFlashAttribute("successMessage", "テスト: パラメータを受信しました"); 

 

        } catch (Exception e) { 

            System.err.println("=== 売上登録エラー ==="); 

            e.printStackTrace(); 

            redirectAttributes.addFlashAttribute("errorMessage", "エラーが発生しました: " + e.getMessage()); 

        } 

 

        return "redirect:/sales"; 

    } 

 

    @GetMapping("/check/{salesId}") 

    @ResponseBody 

    public Map<String, Boolean> checkSalesId(@PathVariable Integer salesId) { 

        Map<String, Boolean> response = new HashMap<>(); 

        response.put("exists", false);  // 一時的に常にfalseを返す 

        return response; 

    } 

 

    @GetMapping("/history") 

    public String salesHistory(Model model) { 

        // 一時的に空のリストを返す 

        model.addAttribute("salesHistory", new ArrayList<>()); 

        return "sales_history"; 

    } 

} 