package com.example.coinlens.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Coin implements Serializable {
    private String currency;

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @SerializedName("id")
    private String id;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("name")
    private String name;

    @SerializedName("image")
    private String image;

    @SerializedName("current_price")
    private double currentPrice;

    @SerializedName("market_cap")
    private double marketCap;

    @SerializedName("total_volume")
    private double totalVolume;

    @SerializedName("high_24h")
    private double high24h;

    @SerializedName("low_24h")
    private double low24h;

    @SerializedName("price_change_percentage_24h")
    private double priceChangePercentage24h;

    public Coin() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getMarketCap() { return marketCap; }
    public void setMarketCap(double marketCap) { this.marketCap = marketCap; }

    public double getTotalVolume() { return totalVolume; }
    public void setTotalVolume(double totalVolume) { this.totalVolume = totalVolume; }

    public double getHigh24h() { return high24h; }
    public void setHigh24h(double high24h) { this.high24h = high24h; }

    public double getLow24h() { return low24h; }
    public void setLow24h(double low24h) { this.low24h = low24h; }

    public double getPriceChangePercentage24h() { return priceChangePercentage24h; }
    public void setPriceChangePercentage24h(double priceChangePercentage24h) { this.priceChangePercentage24h = priceChangePercentage24h; }
}
