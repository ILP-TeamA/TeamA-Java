package com.teama.javaproject.controller;

import com.teama.javaproject.service.WeeklyDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Map;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/weekly")
public class WeeklyDetailController {
    
    @Autowired
    private WeeklyDetailService weeklyDetailService;
    
    // 週選択画面
    @GetMapping("/calendar")
    public String weeklyCalendar(HttpSession session, Model model,
                                @RequestParam(required = false) String year,
                                @RequestParam(required = false) String month) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        
        int currentYear = year != null ? Integer.parseInt(year) : LocalDate.now().getYear();
        int currentMonth = month != null ? Integer.parseInt(month) : LocalDate.now().getMonthValue();
        
        // 月の週一覧を取得
        List<Map<String, Object>> weeklyData = weeklyDetailService.getMonthlyWeeks(currentYear, currentMonth);
        
        model.addAttribute("weeklyData", weeklyData);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("currentMonth", currentMonth);
        
        return "weekly/calendar";
    }
    
    // 週詳細画面
    @GetMapping("/detail")
    public String weeklyDetail(HttpSession session, Model model,
                              @RequestParam String startDate) {
        if (!isLoggedIn(session)) {
            return "redirect:/";
        }
        
        try {
            LocalDate weekStart = LocalDate.parse(startDate);
            LocalDate weekEnd = weekStart.plusDays(6);
            
            // 週の詳細データ取得
            Map<String, Object> weeklyDetail = weeklyDetailService.getWeeklyDetail(weekStart, weekEnd);
            
            model.addAttribute("weeklyDetail", weeklyDetail);
            model.addAttribute("weekStart", weekStart);
            model.addAttribute("weekEnd", weekEnd);
            
            return "weekly/detail";
        } catch (Exception e) {
            model.addAttribute("error", "データの取得に失敗しました: " + e.getMessage());
            return "weekly/calendar";
        }
    }
    
    // 特定日の詳細データ取得（AJAX用）
    @GetMapping("/api/day-detail/{date}")
    @ResponseBody
    public Map<String, Object> getDayDetail(@PathVariable String date, HttpSession session) {
        if (!isLoggedIn(session)) {
            return Map.of("error", "Unauthorized");
        }
        
        try {
            LocalDate targetDate = LocalDate.parse(date);
            return weeklyDetailService.getDayDetail(targetDate);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    // 週データ更新（AJAX用）
    @GetMapping("/api/week-data")
    @ResponseBody
    public Map<String, Object> getWeekData(@RequestParam String startDate,
                                          @RequestParam String endDate,
                                          HttpSession session) {
        if (!isLoggedIn(session)) {
            return Map.of("error", "Unauthorized");
        }
        
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            return weeklyDetailService.getWeeklyDetail(start, end);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
    
    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
}