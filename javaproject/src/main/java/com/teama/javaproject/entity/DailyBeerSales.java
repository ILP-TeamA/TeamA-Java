package com.teama.javaproject.entity;

import jakarta.persistence.*;

/**
 * 日別ビール販売明細エンティティ
 * daily_beer_sales テーブルに対応
 */
@Entity
@Table(name = "daily_beer_sales")
public class DailyBeerSales {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;
    
    @Column(name = "sales_id")
    private Integer salesId;
    
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "revenue")
    private Integer revenue;
    
    @Column(name = "create_by")
    private Integer createBy;
    
    // products テーブルとの結合用
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    // デフォルトコンストラクタ
    public DailyBeerSales() {}
    
    // コンストラクタ
    public DailyBeerSales(Integer salesId, Integer productId, Integer quantity, Integer revenue, Integer createBy) {
        this.salesId = salesId;
        this.productId = productId;
        this.quantity = quantity;
        this.revenue = revenue;
        this.createBy = createBy;
    }
    
    // Getter/Setter
    public Long getDetailId() {
        return detailId;
    }
    
    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }
    
    public Integer getSalesId() {
        return salesId;
    }
    
    public void setSalesId(Integer salesId) {
        this.salesId = salesId;
    }
    
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Integer getRevenue() {
        return revenue;
    }
    
    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }
    
    public Integer getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
}