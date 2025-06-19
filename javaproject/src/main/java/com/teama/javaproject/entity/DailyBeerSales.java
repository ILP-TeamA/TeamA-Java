package com.teama.javaproject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "daily_beer_sales")
@Data
public class DailyBeerSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    private Integer salesId;
    private Integer productId;
    private Integer quantity;
    private Integer revenue;
    private Integer createBy;
}

