package com.teama.javaproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_beer_sales")  // ← テーブル名を修正
@Data
@NoArgsConstructor
public class SalesRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_id")  // ← カラム名を修正
    private Long dailyId;  // ← フィールド名も変更
    
    @Column(name = "sales_id", nullable = false)
    private Integer salesId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "revenue", nullable = false)
    private Integer revenue;
    
    @Column(name = "create_by", nullable = false)
    private Integer createBy;
    
    @Column(name = "create_at")  // ← カラム名を修正
    private LocalDateTime createAt;  // ← フィールド名も変更
    
    // コンストラクタ
    public SalesRecord(Integer salesId, Product product, Integer quantity, Integer createBy) {
        this.salesId = salesId;
        this.product = product;
        this.quantity = quantity;
        this.revenue = product.getUnitPrice() * quantity;
        this.createBy = createBy;
        this.createAt = LocalDateTime.now();  // ← 修正
    }
    
    // 売上金額の再計算メソッド
    public void recalculateRevenue() {
        if (this.product != null && this.quantity != null) {
            this.revenue = this.product.getUnitPrice() * this.quantity;
        }
    }
    
    // getter メソッドを手動で定義（Lombokで自動生成されないもの）
    public Long getDetailId() {
        return dailyId;  // ← 互換性のため
    }
    
    public Integer getSalesId() {
        return salesId;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public int getRevenue() {
        return this.quantity * this.product.getUnitPrice();
    }
}