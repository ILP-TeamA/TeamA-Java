package com.teama.javaproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_records")
@Data
@NoArgsConstructor
public class SalesRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate salesDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Integer totalAmount;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public SalesRecord(LocalDate salesDate, Product product, Integer quantity) {
        this.salesDate = salesDate;
        this.product = product;
        this.quantity = quantity;
        this.totalAmount = product.getUnitPrice() * quantity;
        this.createdAt = LocalDateTime.now();
    }
    
    // 合計金額の再計算メソッド
    public void recalculateTotalAmount() {
        if (this.product != null && this.quantity != null) {
            this.totalAmount = this.product.getUnitPrice() * this.quantity;
        }
    }
}