package com.teama.javaproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_records")
@Data
@NoArgsConstructor
public class SalesRecord {
    @Id // 主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;
    
    @Column(name = "sales_id", nullable = false) // 販売日ID（外部キー）
    private Integer salesId;
    
    @ManyToOne(fetch = FetchType.LAZY) // 商品との多対一関係
    @JoinColumn(name = "product_id", nullable = false) // 外部キー制約
    private Product product;
    
    @Column(nullable = false) // 販売数量（カップ数）
    private Integer quantity;
    
    @Column(nullable = false) // 売上金額（円）
    private Integer revenue;
    
    @Column(name = "create_by", nullable = false) // 登録者（外部キー）
    private Integer createBy;
    
    @Column(name = "created_at") // 作成日時（システム用）
    private LocalDateTime createdAt;
    
    public SalesRecord(Integer salesId, Product product, Integer quantity, Integer createBy) {
        this.salesId = salesId;
        this.product = product;
        this.quantity = quantity;
        this.revenue = product.getUnitPrice() * quantity;
        this.createBy = createBy;
        this.createdAt = LocalDateTime.now();
    }
    
    // 売上金額の再計算メソッド
    public void recalculateRevenue() {
        if (this.product != null && this.quantity != null) {
            this.revenue = this.product.getUnitPrice() * this.quantity;
        }
    }
    
    public Long getDetailId() {
        return detailId;
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