package com.teama.javaproject.entity;
import java.time.LocalDate;

// TODO: データベース接続後にアノテーションのコメントアウトを削除
// @Entity
// @Table(name = "weather_history")
public class WeatherHistory {

    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(name = "date", nullable = false)
    private LocalDate date;

    // @Column(name = "weather_type", nullable = false, length = 20)
    private String weatherType; // 晴れ、曇り、雨、雪など

    // @Column(name = "max_temperature", nullable = false)
    private Integer maxTemperature; // 最高気温（℃）

    // @Column(name = "min_temperature", nullable = false)
    private Integer minTemperature; // 最低気温（℃）

    // @Column(name = "wind_speed", nullable = false)
    private Double windSpeed; // 風速（m/s）

    // デフォルトコンストラクタ
    public WeatherHistory() {}

    // 全フィールドコンストラクタ
    public WeatherHistory(LocalDate date, String weatherType, Integer maxTemperature, 
                         Integer minTemperature, Double windSpeed) {
        this.date = date;
        this.weatherType = weatherType;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.windSpeed = windSpeed;
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

    public String getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(String weatherType) {
        this.weatherType = weatherType;
    }

    public Integer getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Integer maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Integer getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Integer minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * 天気アイコンを取得（ワイヤーフレームに対応）
     */
    public String getWeatherIcon() {
        switch (weatherType) {
            case "晴れ":
                return "☀️";
            case "曇り":
                return "☁️";
            case "雨":
                return "☔";
            case "雪":
                return "❄️";
            default:
                return "🌤️";
        }
    }

    /**
     * 温度範囲を表示用文字列で取得
     */
    public String getTemperatureRange() {
        return minTemperature + "℃ / " + maxTemperature + "℃";
    }

    /**
     * 風速を表示用文字列で取得
     */
    public String getWindSpeedDisplay() {
        return String.format("%.1f m/s", windSpeed);
    }

    @Override
    public String toString() {
        return "WeatherHistory{" +
                "id=" + id +
                ", date=" + date +
                ", weatherType='" + weatherType + '\'' +
                ", maxTemperature=" + maxTemperature +
                ", minTemperature=" + minTemperature +
                ", windSpeed=" + windSpeed +
                '}';
    }
}

