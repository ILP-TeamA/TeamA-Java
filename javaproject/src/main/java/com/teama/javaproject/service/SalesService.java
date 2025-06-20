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

@Service
@Transactional
public class SalesService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private SalesRecordRepository salesRecordRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public void registerSalesData(LocalDate salesDate, Map<Long, Integer> productSales) {
        for (Map.Entry<Long, Integer> entry : productSales.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            if (quantity > 0) {
                if (salesRecordRepository.existsBySalesDateAndProduct_Id(salesDate, productId)) {
                    throw new RuntimeException("この日付の" + getProductName(productId) + "は既に登録されています");
                }
                
                Optional<Product> productOpt = productRepository.findById(productId.intValue());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    SalesRecord record = new SalesRecord(salesDate, product, quantity);
                    salesRecordRepository.save(record);
                } else {
                    throw new RuntimeException("商品が見つかりません: ID " + productId);
                }
            }
        }
    }
    
    public Map<LocalDate, List<SalesRecord>> getSalesDataGroupedByDate() {
        List<SalesRecord> allRecords = salesRecordRepository.findAll();
        return allRecords.stream()
                .collect(Collectors.groupingBy(
                    SalesRecord::getSalesDate,
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }
    
    public List<SalesRecord> getAllSalesRecords() {
        return salesRecordRepository.findBySalesDateBetweenOrderBySalesDateDescProductIdAsc(
            LocalDate.of(2020, 1, 1), LocalDate.now().plusDays(1));
    }
    
    public boolean hasSalesDataForDate(LocalDate date) {
        return !salesRecordRepository.findBySalesDateOrderByProductIdAsc(date).isEmpty();
    }
    
    private String getProductName(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId.intValue());
        return productOpt.map(Product::getName).orElse("不明な商品");
    }
}