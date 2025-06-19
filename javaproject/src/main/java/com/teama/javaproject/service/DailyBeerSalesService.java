//  package com.teama.javaproject.service;

// import com.teama.javaproject.entity.DailyBeerSales;
// import com.teama.javaproject.repository.DailyBeerSalesRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import java.time.LocalDate;
// import java.util.List;

// @Service
// public class DailyBeerSalesService {
    
//     @Autowired
//     private DailyBeerSalesRepository repository;
    
//     // 販売データの登録
//     public String registerSales(LocalDate date, Integer salesAmount) {
//         try {
//             // 入力値のバリデーション
//             String validationError = validateInput(date, salesAmount);
//             if (validationError != null) {
//                 return validationError;
//             }
            
//             // 同じ日付のデータが既に存在するかチェック
//             if (repository.existsByDate(date)) {
//                 return "この日付のデータは既に登録されています。";
//             }
            
//             // データ保存
//             DailyBeerSales sales = new DailyBeerSales(date, salesAmount);
//             repository.save(sales);
            
//             return null; // 成功時はnullを返す
            
//         } catch (Exception e) {
//             return "データの登録中にエラーが発生しました。";
//         }
//     }
    
//     // 全ての販売データを取得
//     public List<DailyBeerSales> getAllSales() {
//         return repository.findAllByOrderByDateDesc();
//     }
    
//     // 入力値のバリデーション
//     private String validateInput(LocalDate date, Integer salesAmount) {
//         // 日付のチェック
//         if (date == null) {
//             return "日付を入力してください。";
//         }
        
//         // 未来の日付チェック
//         if (date.isAfter(LocalDate.now())) {
//             return "未来の日付は登録できません。";
//         }
        
//         // 販売本数のチェック
//         if (salesAmount == null) {
//             return "販売本数を入力してください。";
//         }
        
//         if (salesAmount < 1 || salesAmount > 9999) {
//             return "販売本数は1〜9999の範囲で入力してください。";
//         }
        
//         return null; // バリデーション成功
//     }
// }
