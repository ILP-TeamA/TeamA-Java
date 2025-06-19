package com.teama.javaproject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "predictions")
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate predictionDate;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer predictedQuantity;

    // --- getter/setter ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDate predictionDate) {
        this.predictionDate = predictionDate;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getPredictedQuantity() {
        return predictedQuantity;
    }

    public void setPredictedQuantity(Integer predictedQuantity) {
        this.predictedQuantity = predictedQuantity;
    }
}
