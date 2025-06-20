package com.teama.javaproject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 天気履歴エンティティ
 * weather_history テーブルに対応
 */
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
    
    // デフォルトコンストラクタ
    public WeatherHistory() {}
    
    // コンストラクタ
    public WeatherHistory(LocalDate date, Float avgTemperature, Float maxTemperature, 
                         Float minTemperature, String weatherConditionDay) {
        this.date = date;
        this.avgTemperature = avgTemperature;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.weatherConditionDay = weatherConditionDay;
    }
    
    /**
     * 天気アイコンを取得
     */
    public String getWeatherIcon() {
        if (weatherConditionDay == null) return "❓";
        
        switch (weatherConditionDay.toLowerCase()) {
            case "晴れ":
            case "快晴":
                return "☀️";
            case "曇り":
            case "薄曇り":
                return "☁️";
            case "雨":
            case "小雨":
            case "大雨":
                return "🌧️";
            case "雪":
            case "大雪":
                return "❄️";
            case "晴れ時々曇り":
                return "🌤️";
            default:
                return "🌥️";
        }
    }
    
    /**
     * 気温範囲の表示文字列を取得
     */
    public String getTemperatureRange() {
        if (minTemperature == null || maxTemperature == null) {
            return "データなし";
        }
        return String.format("%.0f℃ / %.0f℃", minTemperature, maxTemperature);
    }
    
    /**
     * 風速表示（仮想データとして平均湿度を使用）
     */
    public String getWindSpeedDisplay() {
        if (avgHumidity == null) return "データなし";
        // 湿度から風速を推定（仮想計算）
        double windSpeed = avgHumidity / 20.0;
        return String.format("%.1f m/s", windSpeed);
    }
    
    // Getter/Setter
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Float getAvgTemperature() {
        return avgTemperature;
    }
    
    public void setAvgTemperature(Float avgTemperature) {
        this.avgTemperature = avgTemperature;
    }
    
    public Float getMaxTemperature() {
        return maxTemperature;
    }
    
    public void setMaxTemperature(Float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }
    
    public Float getMinTemperature() {
        return minTemperature;
    }
    
    public void setMinTemperature(Float minTemperature) {
        this.minTemperature = minTemperature;
    }
    
    public Float getTotalRainfall() {
        return totalRainfall;
    }
    
    public void setTotalRainfall(Float totalRainfall) {
        this.totalRainfall = totalRainfall;
    }
    
    public Float getSunshineHours() {
        return sunshineHours;
    }
    
    public void setSunshineHours(Float sunshineHours) {
        this.sunshineHours = sunshineHours;
    }
    
    public Float getAvgHumidity() {
        return avgHumidity;
    }
    
    public void setAvgHumidity(Float avgHumidity) {
        this.avgHumidity = avgHumidity;
    }
    
    public String getWeatherConditionDay() {
        return weatherConditionDay;
    }
    
    public void setWeatherConditionDay(String weatherConditionDay) {
        this.weatherConditionDay = weatherConditionDay;
    }
    
    public String getWeatherConditionNight() {
        return weatherConditionNight;
    }
    
    public void setWeatherConditionNight(String weatherConditionNight) {
        this.weatherConditionNight = weatherConditionNight;
    }
}