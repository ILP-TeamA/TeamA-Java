package com.teama.javaproject.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * 商品エンティティ
 * products テーブルに対応
 */
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "unit_price")
    private Integer unitPrice;
    
    @Column(name = "jancode")
    private String janCode;
    
    // daily_beer_sales との一対多関係
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyBeerSales> dailyBeerSales;
    
    // デフォルトコンストラクタ
    public Product() {}
    
    // コンストラクタ
    public Product(String name, Integer unitPrice, String janCode) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.janCode = janCode;
    }
    
    // Getter/Setter
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(Integer unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String getJanCode() {
        return janCode;
    }
    
    public void setJanCode(String janCode) {
        this.janCode = janCode;
    }
    
    public List<DailyBeerSales> getDailyBeerSales() {
        return dailyBeerSales;
    }
    
    public void setDailyBeerSales(List<DailyBeerSales> dailyBeerSales) {
        this.dailyBeerSales = dailyBeerSales;
    }

    
}