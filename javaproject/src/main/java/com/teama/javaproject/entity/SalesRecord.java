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
    @Id// 主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY)//
    private Long id;
    
    @Column(nullable = false)// 売上日
    private LocalDate salesDate;
    
    @ManyToOne(fetch = FetchType.LAZY)// 商品との多対一関係
    @JoinColumn(name = "product_id", nullable = false)// 外部キー制約
    private Product product;
    
    @Column(nullable = false)// 数量
    private Integer quantity;
    
    @Column(nullable = false)// 合計金額
    private Integer totalAmount;
    
    @Column(nullable = false)// 作成日時
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

    public LocalDate getSalesDate() {
        return salesDate;
    }
}