package com.teama.javaproject.service;

import com.teama.javaproject.entity.Product;
import com.teama.javaproject.entity.SalesRecord;
import com.teama.javaproject.repository.ProductRepository;
import com.teama.javaproject.repository.SalesRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

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
    
    public void registerSalesData(Integer salesId, Map<Long, Integer> productSales, Integer createBy) {
        for (Map.Entry<Long, Integer> entry : productSales.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            if (quantity > 0) {
                if (salesRecordRepository.existsBySalesIdAndProduct_Id(salesId, productId)) {
                    throw new RuntimeException("この販売ID(" + salesId + ")の" + getProductName(productId) + "は既に登録されています");
                }
                
                Optional<Product> productOpt = productRepository.findById(productId.intValue());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    SalesRecord record = new SalesRecord(salesId, product, quantity, createBy);
                    salesRecordRepository.save(record);
                } else {
                    throw new RuntimeException("商品が見つかりません: ID " + productId);
                }
            }
        }
    }
    
    public Map<Integer, List<SalesRecord>> getSalesDataGroupedBySalesId() {
        List<SalesRecord> allRecords = salesRecordRepository.findAll();
        return allRecords.stream()
                .collect(Collectors.groupingBy(
                    SalesRecord::getSalesId,
                    LinkedHashMap::new,
                    Collectors.toList()
                ));
    }
    
    public List<SalesRecord> getAllSalesRecords() {
        return salesRecordRepository.findAllByOrderBySalesIdDescProduct_IdAsc();
    }
    
    public List<SalesRecord> getSalesRecordsBySalesId(Integer salesId) {
        return salesRecordRepository.findBySalesIdOrderByProduct_IdAsc(salesId);
    }
    
    public boolean hasSalesDataForSalesId(Integer salesId) {
        return !salesRecordRepository.findBySalesIdOrderByProduct_IdAsc(salesId).isEmpty();
    }
    
    public List<SalesRecord> getSalesRecordsByProductId(Long productId) {
        return salesRecordRepository.findByProduct_IdOrderBySalesIdDesc(productId);
    }
    
    private String getProductName(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId.intValue());
        return productOpt.map(Product::getName).orElse("不明な商品");
    }

    public List<Integer> getAllSalesIds() {
        return salesRecordRepository.findAll()
            .stream()
            .map(SalesRecord::getSalesId)
            .distinct()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
        
    }

    // 既存データを削除して新規保存
    public void updateSalesData(Integer salesId, Map<Long, Integer> productSales, Integer createBy) {
        salesRecordRepository.deleteBySalesId(salesId);
        registerSalesData(salesId, productSales, createBy);
    }
    
    public List<SalesRecord> getSalesRecordsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);
        return salesRecordRepository.findByCreatedAtBetweenOrderByProduct_IdAsc(start, end);
    }
    
}