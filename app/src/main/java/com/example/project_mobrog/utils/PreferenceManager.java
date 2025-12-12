package com.example.project_mobrog.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * SharedPreferences wrapper for storing user preferences and search history
 */
public class PreferenceManager {
    
    private static final String PREF_NAME = "cloudly_prefs";
    private static final String KEY_LAST_CITY = "last_city";
    private static final String KEY_SEARCH_HISTORY = "search_history";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final int MAX_HISTORY_SIZE = 10;
    
    private SharedPreferences prefs;
    private Gson gson;
    
    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    /**
     * Save last searched city
     */
    public void saveLastCity(String city) {
        prefs.edit().putString(KEY_LAST_CITY, city).apply();
        addToSearchHistory(city);
    }
    
    /**
     * Get last searched city
     */
    public String getLastCity() {
        return prefs.getString(KEY_LAST_CITY, null);
    }
    
    /**
     * Add city to search history
     */
    public void addToSearchHistory(String city) {
        List<String> history = getSearchHistory();
        
        // Remove if already exists (to move to top)
        history.remove(city);
        
        // Add to beginning
        history.add(0, city);
        
        // Limit size
        if (history.size() > MAX_HISTORY_SIZE) {
            history = history.subList(0, MAX_HISTORY_SIZE);
        }
        
        // Save
        String json = gson.toJson(history);
        prefs.edit().putString(KEY_SEARCH_HISTORY, json).apply();
    }
    
    /**
     * Get search history
     */
    public List<String> getSearchHistory() {
        String json = prefs.getString(KEY_SEARCH_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> history = gson.fromJson(json, type);
        return history != null ? history : new ArrayList<>();
    }
    
    /**
     * Clear search history
     */
    public void clearSearchHistory() {
        prefs.edit().remove(KEY_SEARCH_HISTORY).apply();
    }
    
    /**
     * Set dark mode preference
     */
    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }
    
    /**
     * Get dark mode preference
     */
    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }
}
