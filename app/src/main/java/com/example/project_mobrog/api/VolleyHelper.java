package com.example.project_mobrog.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.project_mobrog.model.WeatherResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Helper class demonstrating Volley usage for simple GET requests
 * Use this for simple, quick API calls
 */
public class VolleyHelper {
    
    private static final String TAG = "VolleyHelper";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    
    private static VolleyHelper instance;
    private RequestQueue requestQueue;
    private Context context;
    private Gson gson;
    
    private VolleyHelper(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = Volley.newRequestQueue(this.context);
        this.gson = new Gson();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized VolleyHelper getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyHelper(context);
        }
        return instance;
    }
    
    /**
     * Get current weather using Volley
     * Demonstrates: Simple GET request with Volley
     * 
     * @param city City name
     * @param apiKey API key
     * @param listener Success callback
     * @param errorListener Error callback
     */
    public void getCurrentWeather(String city, String apiKey, 
                                   final WeatherResponseListener listener,
                                   final Response.ErrorListener errorListener) {
        
        String url = BASE_URL + "weather?q=" + city + "&appid=" + apiKey + "&units=metric";
        
        Log.d(TAG, "Volley GET: " + url);
        
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Volley Response: " + response.toString());
                        // Parse JSON using Gson
                        WeatherResponse weatherResponse = gson.fromJson(response.toString(), WeatherResponse.class);
                        listener.onResponse(weatherResponse);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        errorListener.onErrorResponse(error);
                    }
                }
        );
        
        requestQueue.add(request);
    }
    
    /**
     * Cancel all pending requests
     */
    public void cancelAllRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
    
    /**
     * Callback interface for weather response
     */
    public interface WeatherResponseListener {
        void onResponse(WeatherResponse response);
    }
}
