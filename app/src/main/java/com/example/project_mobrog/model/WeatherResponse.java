package com.example.project_mobrog.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model class for OpenWeatherMap Current Weather API response
 * https://api.openweathermap.org/data/2.5/weather
 */
public class WeatherResponse {
    
    @SerializedName("coord")
    private Coord coord;
    
    @SerializedName("weather")
    private List<Weather> weather;
    
    @SerializedName("main")
    private Main main;
    
    @SerializedName("wind")
    private Wind wind;
    
    @SerializedName("clouds")
    private Clouds clouds;
    
    @SerializedName("sys")
    private Sys sys;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("cod")
    private int cod;
    
    @SerializedName("dt")
    private long dt;
    
    @SerializedName("timezone")
    private int timezone;
    
    // Getters
    public Coord getCoord() { return coord; }
    public List<Weather> getWeather() { return weather; }
    public Main getMain() { return main; }
    public Wind getWind() { return wind; }
    public Clouds getClouds() { return clouds; }
    public Sys getSys() { return sys; }
    public String getName() { return name; }
    public int getCod() { return cod; }
    public long getDt() { return dt; }
    public int getTimezone() { return timezone; }
    
    // Inner classes
    public static class Coord {
        @SerializedName("lon")
        private double lon;
        @SerializedName("lat")
        private double lat;
        
        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }
    
    public static class Weather {
        @SerializedName("id")
        private int id;
        @SerializedName("main")
        private String main;
        @SerializedName("description")
        private String description;
        @SerializedName("icon")
        private String icon;
        
        public int getId() { return id; }
        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }
    
    public static class Main {
        @SerializedName("temp")
        private double temp;
        @SerializedName("feels_like")
        private double feelsLike;
        @SerializedName("temp_min")
        private double tempMin;
        @SerializedName("temp_max")
        private double tempMax;
        @SerializedName("pressure")
        private int pressure;
        @SerializedName("humidity")
        private int humidity;
        
        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
    }
    
    public static class Wind {
        @SerializedName("speed")
        private double speed;
        @SerializedName("deg")
        private int deg;
        
        public double getSpeed() { return speed; }
        public int getDeg() { return deg; }
    }
    
    public static class Clouds {
        @SerializedName("all")
        private int all;
        
        public int getAll() { return all; }
    }
    
    public static class Sys {
        @SerializedName("country")
        private String country;
        @SerializedName("sunrise")
        private long sunrise;
        @SerializedName("sunset")
        private long sunset;
        
        public String getCountry() { return country; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }
}
