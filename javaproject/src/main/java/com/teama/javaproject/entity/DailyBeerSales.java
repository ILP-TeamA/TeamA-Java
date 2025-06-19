package com.teama.javaproject.entity;

// import javax.persistence.*;
import java.time.LocalDate;

// TODO: データベース接続後にアノテーションのコメントアウトを削除
// @Entity
// @Table(name = "daily_beer_sales")
public class DailyBeerSales {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(name = "date", nullable = false)
    private LocalDate date;

    // @Column(name = "white_beer_sales", nullable = false)
    private Integer whiteBeerSales;

    // @Column(name = "lager_sales", nullable = false)
    private Integer lagerSales;

    // @Column(name = "pale_ale_sales", nullable = false)
    private Integer paleAleSales;

    // @Column(name = "fruit_beer_sales", nullable = false)
    private Integer fruitBeerSales;

    // @Column(name = "stout_sales", nullable = false)
    private Integer stoutSales;

    // @Column(name = "ipa_sales", nullable = false)
    private Integer ipaSales;

    // デフォルトコンストラクタ
    public DailyBeerSales() {}

    // 全フィールドコンストラクタ
    public DailyBeerSales(LocalDate date, Integer whiteBeerSales, Integer lagerSales, 
                         Integer paleAleSales, Integer fruitBeerSales, Integer stoutSales, Integer ipaSales) {
        this.date = date;
        this.whiteBeerSales = whiteBeerSales;
        this.lagerSales = lagerSales;
        this.paleAleSales = paleAleSales;
        this.fruitBeerSales = fruitBeerSales;
        this.stoutSales = stoutSales;
        this.ipaSales = ipaSales;
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getWhiteBeerSales() {
        return whiteBeerSales != null ? whiteBeerSales : 0;
    }

    public void setWhiteBeerSales(Integer whiteBeerSales) {
        this.whiteBeerSales = whiteBeerSales;
    }

    public Integer getLagerSales() {
        return lagerSales != null ? lagerSales : 0;
    }

    public void setLagerSales(Integer lagerSales) {
        this.lagerSales = lagerSales;
    }

    public Integer getPaleAleSales() {
        return paleAleSales != null ? paleAleSales : 0;
    }

    public void setPaleAleSales(Integer paleAleSales) {
        this.paleAleSales = paleAleSales;
    }

    public Integer getFruitBeerSales() {
        return fruitBeerSales != null ? fruitBeerSales : 0;
    }

    public void setFruitBeerSales(Integer fruitBeerSales) {
        this.fruitBeerSales = fruitBeerSales;
    }

    public Integer getStoutSales() {
        return stoutSales != null ? stoutSales : 0;
    }

    public void setStoutSales(Integer stoutSales) {
        this.stoutSales = stoutSales;
    }

    public Integer getIpaSales() {
        return ipaSales != null ? ipaSales : 0;
    }

    public void setIpaSales(Integer ipaSales) {
        this.ipaSales = ipaSales;
    }

    /**
     * 日別総販売数を計算
     */
    public Integer getTotalSales() {
        return getWhiteBeerSales() + getLagerSales() + getPaleAleSales() + 
               getFruitBeerSales() + getStoutSales() + getIpaSales();
    }

    @Override
    public String toString() {
        return "DailyBeerSales{" +
                "id=" + id +
                ", date=" + date +
                ", whiteBeerSales=" + whiteBeerSales +
                ", lagerSales=" + lagerSales +
                ", paleAleSales=" + paleAleSales +
                ", fruitBeerSales=" + fruitBeerSales +
                ", stoutSales=" + stoutSales +
                ", ipaSales=" + ipaSales +
                ", totalSales=" + getTotalSales() +
                '}';
    }
}


