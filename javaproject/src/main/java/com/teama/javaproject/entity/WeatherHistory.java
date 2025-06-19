package com.teama.javaproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "weather_history")
@Data
public class WeatherHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Float avgTemperature;
    private Float maxTemperature;
    private Float minTemperature;
    private Float totalRainfall;
    private Float sunshineHours;
    private Float avgHumidity;
}

