package com.teama.javaproject.service;

import com.teama.javaproject.entity.Product;
import com.teama.javaproject.entity.SalesRecord;
import com.teama.javaproject.repository.ProductRepository;
import com.teama.javaproject.repository.SalesRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service//このクラスがサービス層のコンポーネントであることを示す
@Transactional//このクラスのメソッドをトランザクション管理下に置く
public class SalesService {
    
    // リポジトリのインジェクション依存性注入？
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private SalesRecordRepository salesRecordRepository;
    
    // 全商品取得
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // 売上データ一括登録
    //　販売日と商品ごとの販売数量を受け取り、売上データを登録する
    public void registerSalesData(LocalDate salesDate, Map<Long, Integer> productSales) {
        for (Map.Entry<Long, Integer> entry : productSales.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            if (quantity > 0) {
                // 重複チェック販売数量が0より大きい場合のみ処理。おなじ販売日と商品IDを確認
                if (salesRecordRepository.existsBySalesDateAndProduct_Id(salesDate, productId)) {
                    throw new RuntimeException("この日付の" + getProductName(productId) + "は既に登録されています");
                }
                
                // findById: データベースから指定IDの商品を検索
                //Optional<Product>: 商品が見つかるかどうか不確実な場合に使用する型
                //productOpt: 商品が存在する場合はその商品を、存在しない場合は空を保持
                Optional<Product> productOpt = productRepository.findById(productId);
                if (productOpt.isPresent()) {//isPresentが値を持っている場合に実行
                    Product product = productOpt.get();// get: Optionalから値を取得
                    SalesRecord record = new SalesRecord(salesDate, product, quantity);// 新しい売上データを作成
                    salesRecordRepository.save(record);// save: 売上データをデータベースに保存
                } else {
                    throw new RuntimeException("商品が見つかりません: ID " + productId);
                }
            }
        }
    }
    
    // 売上データ取得（日付別グループ化）
    public Map<LocalDate, List<SalesRecord>> getSalesDataGroupedByDate() {//キーが日付、値が売上データのリスト
        List<SalesRecord> allRecords = salesRecordRepository.findAll();// 全ての売上データを取得、allRecordsに格納
        return allRecords.stream()//リストをstreamに変換
                .collect(Collectors.groupingBy(
                    SalesRecord::getSalesDate,//キー、売り上げ記録から日付を取得
                    LinkedHashMap::new,//LinkedHashMapを使用して順序を保持（日付順に表示する
                    Collectors.toList()//同じ日付の売上データをリストで収集
                ));
    }
    
    // 売上データ取得（全件、日付降順）
    public List<SalesRecord> getAllSalesRecords() {
        return salesRecordRepository.findBySalesDateBetweenOrderBySalesDateDescProductIdAsc(
            LocalDate.of(2020, 1, 1), LocalDate.now().plusDays(1));
    }
    
    // 特定日の売上データ存在チェックfindbyで取得リストが空か確認、空であれば!によってfaileseを返す
    public boolean hasSalesDataForDate(LocalDate date) {
        return !salesRecordRepository.findBySalesDateOrderByProductIdAsc(date).isEmpty();
    }
    
    // 商品が見つからない場合のメソッド
    private String getProductName(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(Product::getName).orElse("不明な商品");
    }
}