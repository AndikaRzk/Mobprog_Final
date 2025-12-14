package com.example.project_mobrog.model;

public class FavoriteCity {
    private int id;
    private String cityName;
    private String country;

    public FavoriteCity() {
    }

    public FavoriteCity(int id, String cityName, String country) {
        this.id = id;
        this.cityName = cityName;
        this.country = country;
    }

    public FavoriteCity(String cityName, String country) {
        this.cityName = cityName;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
