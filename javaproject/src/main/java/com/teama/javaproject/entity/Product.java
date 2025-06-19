// package com.teama.javaproject.entity;

// public class Product {
    
// }

package com.teama.javaproject.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Integer price;
    
    // コンストラクタ
    public Product() {}
    
    public Product(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
    
    // ゲッター・セッター
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}