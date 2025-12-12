package com.example.project_mobrog.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.project_mobrog.model.WeatherResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Helper class demonstrating direct OkHttp usage for manual HTTP requests
 * Use this when you need more control over the request
 */
public class OkHttpHelper {
    
    private static final String TAG = "OkHttpHelper";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    
    private static OkHttpHelper instance;
    private OkHttpClient client;
    private Gson gson;
    private Handler mainHandler;
    
    private OkHttpHelper() {
        // Create logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        this.client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        
        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized OkHttpHelper getInstance() {
        if (instance == null) {
            instance = new OkHttpHelper();
        }
        return instance;
    }
    
    /**
     * Get current weather using OkHttp
     * Demonstrates: Manual HTTP request with OkHttp + logging interceptor
     * 
     * @param city City name
     * @param apiKey API key
     * @param listener Callback for success/error
     */
    public void getCurrentWeather(String city, String apiKey, final WeatherCallback listener) {
        String url = BASE_URL + "weather?q=" + city + "&appid=" + apiKey + "&units=metric";
        
        Log.d(TAG, "OkHttp GET: " + url);
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "OkHttp Error: " + e.getMessage());
                // Post to main thread
                mainHandler.post(() -> listener.onError(e.getMessage()));
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "OkHttp Response: " + jsonResponse);
                    
                    // Parse JSON using Gson
                    WeatherResponse weatherResponse = gson.fromJson(jsonResponse, WeatherResponse.class);
                    
                    // Post to main thread
                    mainHandler.post(() -> listener.onSuccess(weatherResponse));
                } else {
                    mainHandler.post(() -> listener.onError("Error: " + response.code()));
                }
            }
        });
    }
    
    /**
     * Cancel all pending requests
     */
    public void cancelAllRequests() {
        client.dispatcher().cancelAll();
    }
    
    /**
     * Callback interface for weather response
     */
    public interface WeatherCallback {
        void onSuccess(WeatherResponse response);
        void onError(String message);
    }
}
