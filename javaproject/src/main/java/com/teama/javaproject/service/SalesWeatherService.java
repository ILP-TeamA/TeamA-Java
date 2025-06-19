package com.teama.javaproject.service;

import com.teama.javaproject.entity.DailyBeerSales;
import com.teama.javaproject.entity.WeatherHistory;
/*それぞれimportしたらコメントアウト外す */
// import com.teama.javaproject.repository.DailyBeerSalesRepository;
// import com.teama.javaproject.repository.WeatherHistoryRepository;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SalesWeatherService {

    // TODO: データベース接続後にコメントアウトを削除
    // @Autowired
    // private DailyBeerSalesRepository salesRepository;
    
    // @Autowired
    // private WeatherHistoryRepository weatherRepository;

    /**
     * 一週間分の販売実績データを取得
     */
    public List<DailyBeerSales> getWeeklySalesData(LocalDate startDate) {
        // TODO: データベース接続後は以下のコードを使用
        // return salesRepository.findByDateBetween(startDate, startDate.plusDays(6));
        
        // 現在はサンプルデータを返す
        return generateSampleSalesData(startDate);
    }

    /**
     * 一週間分の天気データを取得
     */
    public List<WeatherHistory> getWeeklyWeatherData(LocalDate startDate) {
        // TODO: データベース接続後は以下のコードを使用
        // return weatherRepository.findByDateBetween(startDate, startDate.plusDays(6));
        
        // 現在はサンプルデータを返す
        return generateSampleWeatherData(startDate);
    }

    /**
     * Chart.js用のラベル（日付）を生成
     */
    public String generateChartLabels(LocalDate startDate) {
        StringBuilder labels = new StringBuilder("[");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d");
        
        for (int i = 0; i < 7; i++) {
            if (i > 0) labels.append(",");
            labels.append("'").append(startDate.plusDays(i).format(formatter)).append("'");
        }
        labels.append("]");
        
        return labels.toString();
    }

    /**
     * Chart.js用のデータセットを生成
     */
    public String generateChartData(List<DailyBeerSales> salesData) {
        // ビール種類ごとのデータを準備
        StringBuilder datasets = new StringBuilder("[");
        
        // ビール種類の定義
        String[] beerTypes = {"ホワイトビール", "ラガー", "ペールエール", "フルーツビール", "黒ビール", "IPA"};
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7", "#DDA0DD"};
        
        for (int i = 0; i < beerTypes.length; i++) {
            if (i > 0) datasets.append(",");
            
            datasets.append("{")
                   .append("label: '").append(beerTypes[i]).append("',")
                   .append("data: [");
            
            // 各日の販売データを取得
            for (int day = 0; day < 7; day++) {
                if (day > 0) datasets.append(",");
                int sales = getSalesForBeerType(salesData, day, beerTypes[i]);
                datasets.append(sales);
            }
            
            datasets.append("],")
                   .append("borderColor: '").append(colors[i]).append("',")
                   .append("backgroundColor: '").append(colors[i]).append("',")
                   .append("fill: false,")
                   .append("tension: 0.1")
                   .append("}");
        }
        
        datasets.append("]");
        return datasets.toString();
    }

    /**
     * 指定された日とビール種類の販売数を取得
     */
    private int getSalesForBeerType(List<DailyBeerSales> salesData, int dayIndex, String beerType) {
        if (dayIndex < salesData.size()) {
            DailyBeerSales sales = salesData.get(dayIndex);
            switch (beerType) {
                case "ホワイトビール": return sales.getWhiteBeerSales();
                case "ラガー": return sales.getLagerSales();
                case "ペールエール": return sales.getPaleAleSales();
                case "フルーツビール": return sales.getFruitBeerSales();
                case "黒ビール": return sales.getStoutSales();
                case "IPA": return sales.getIpaSales();
            }
        }
        return 0;
    }

    /**
     * サンプル販売データ生成（データベース接続前の仮データ）
     */
    private List<DailyBeerSales> generateSampleSalesData(LocalDate startDate) {
        List<DailyBeerSales> sampleData = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < 7; i++) {
            DailyBeerSales sales = new DailyBeerSales();
            sales.setDate(startDate.plusDays(i));
            
            // ランダムな販売数を生成（0-300本の範囲）
            sales.setWhiteBeerSales(random.nextInt(301));
            sales.setLagerSales(random.nextInt(301));
            sales.setPaleAleSales(random.nextInt(301));
            sales.setFruitBeerSales(random.nextInt(301));
            sales.setStoutSales(random.nextInt(301));
            sales.setIpaSales(random.nextInt(301));
            
            sampleData.add(sales);
        }
        
        return sampleData;
    }

    /**
     * サンプル天気データ生成（データベース接続前の仮データ）
     */
    private List<WeatherHistory> generateSampleWeatherData(LocalDate startDate) {
        List<WeatherHistory> sampleData = new ArrayList<>();
        Random random = new Random();
        String[] weatherTypes = {"晴れ", "曇り", "雨", "雪"};
        
        for (int i = 0; i < 7; i++) {
            WeatherHistory weather = new WeatherHistory();
            weather.setDate(startDate.plusDays(i));
            weather.setWeatherType(weatherTypes[random.nextInt(weatherTypes.length)]);
            weather.setMaxTemperature(15 + random.nextInt(20)); // 15-35度
            weather.setMinTemperature(5 + random.nextInt(15));  // 5-20度
            weather.setWindSpeed(random.nextDouble() * 10);     // 0-10m/s
            
            sampleData.add(weather);
        }
        
        return sampleData;
    }
}