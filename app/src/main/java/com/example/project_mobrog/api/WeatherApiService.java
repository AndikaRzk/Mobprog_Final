package com.example.project_mobrog.api;

import com.example.project_mobrog.model.ForecastResponse;
import com.example.project_mobrog.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface for OpenWeatherMap API endpoints
 */
public interface WeatherApiService {
    
    /**
     * Get current weather data for a city
     * @param city City name (e.g., "Jakarta")
     * @param apiKey OpenWeatherMap API key
     * @param units Temperature units (metric, imperial, standard)
     * @return Current weather data
     */
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
    
    /**
     * Get 5-day/3-hour forecast for a city
     * @param city City name
     * @param apiKey OpenWeatherMap API key
     * @param units Temperature units
     * @return 5-day forecast data
     */
    @GET("forecast")
    Call<ForecastResponse> getForecast(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
    
    /**
     * Get current weather data by coordinates
     * @param lat Latitude
     * @param lon Longitude
     * @param apiKey OpenWeatherMap API key
     * @param units Temperature units
     * @return Current weather data
     */
    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherByCoordinates(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
    
    /**
     * Get 5-day forecast by coordinates
     * @param lat Latitude
     * @param lon Longitude
     * @param apiKey OpenWeatherMap API key
     * @param units Temperature units
     * @return 5-day forecast data
     */
    @GET("forecast")
    Call<ForecastResponse> getForecastByCoordinates(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}
