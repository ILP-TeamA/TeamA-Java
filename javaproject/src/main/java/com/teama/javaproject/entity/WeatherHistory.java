package com.teama.javaproject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "weather_history")
public class WeatherHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "avg_temperature")
    private Float avgTemperature;

    @Column(name = "max_temperature")
    private Float maxTemperature;

    @Column(name = "min_temperature")
    private Float minTemperature;

    @Column(name = "total_rainfall")
    private Float totalRainfall;

    @Column(name = "sunshine_hours")
    private Float sunshineHours;

    @Column(name = "avg_humidity")
    private Float avgHumidity;

    @Column(name = "weather_condition_day")
    private String weatherConditionDay;

    @Column(name = "weather_condition_night")
    private String weatherConditionNight;

    // --- コンストラクタ ---
    public WeatherHistory() {}

    // --- Getter / Setter ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Float getAvgTemperature() { return avgTemperature; }
    public void setAvgTemperature(Float avgTemperature) { this.avgTemperature = avgTemperature; }

    public Float getMaxTemperature() { return maxTemperature; }
    public void setMaxTemperature(Float maxTemperature) { this.maxTemperature = maxTemperature; }

    public Float getMinTemperature() { return minTemperature; }
    public void setMinTemperature(Float minTemperature) { this.minTemperature = minTemperature; }

    public Float getTotalRainfall() { return totalRainfall; }
    public void setTotalRainfall(Float totalRainfall) { this.totalRainfall = totalRainfall; }

    public Float getSunshineHours() { return sunshineHours; }
    public void setSunshineHours(Float sunshineHours) { this.sunshineHours = sunshineHours; }

    public Float getAvgHumidity() { return avgHumidity; }
    public void setAvgHumidity(Float avgHumidity) { this.avgHumidity = avgHumidity; }

    public String getWeatherConditionDay() { return weatherConditionDay; }
    public void setWeatherConditionDay(String weatherConditionDay) { this.weatherConditionDay = weatherConditionDay; }

    public String getWeatherConditionNight() { return weatherConditionNight; }
    public void setWeatherConditionNight(String weatherConditionNight) { this.weatherConditionNight = weatherConditionNight; }

}
