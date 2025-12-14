package com.example.project_mobrog.api;

import com.example.project_mobrog.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Singleton API client using Retrofit with OkHttp
 * Demonstrates: Retrofit + OkHttp integration
 */
public class ApiClient {
    
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    
    // API Key is loaded from BuildConfig (defined in local.properties)
    // public static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    public static final String API_KEY = "1461716e467289f3450a76a868f3b26b";
    
    private static Retrofit retrofit = null;
    private static WeatherApiService apiService = null;
    
    /**
     * Get OkHttpClient with logging interceptor
     * Demonstrates OkHttp usage with logging
     */
    private static OkHttpClient getOkHttpClient() {
        // Create logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Get Retrofit instance (singleton)
     * Uses OkHttp as HTTP client with Gson converter
     */
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    /**
     * Get WeatherApiService instance (singleton)
     */
    public static WeatherApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(WeatherApiService.class);
        }
        return apiService;
    }
    
    /**
     * Get the base URL for icon images
     * @param iconCode Icon code from API (e.g., "10d")
     * @return Full URL to weather icon
     */
    public static String getIconUrl(String iconCode) {
        return "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
    }
}
