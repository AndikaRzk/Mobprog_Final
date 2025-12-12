package com.example.project_mobrog.utils;

import com.example.project_mobrog.R;

/**
 * Utility class for weather-related helper methods
 */
public class WeatherUtils {
    
    /**
     * Get background drawable based on weather condition
     * @param weatherMain Main weather condition (e.g., "Clear", "Rain", "Clouds")
     * @param isNight Whether it's night time
     * @return Drawable resource ID
     */
    public static int getBackgroundForWeather(String weatherMain, boolean isNight) {
        if (isNight) {
            return R.drawable.bg_night;
        }
        
        if (weatherMain == null) {
            return R.drawable.bg_sunny;
        }
        
        switch (weatherMain.toLowerCase()) {
            case "clear":
                return R.drawable.bg_sunny;
            case "clouds":
                return R.drawable.bg_cloudy;
            case "rain":
            case "drizzle":
                return R.drawable.bg_rainy;
            case "thunderstorm":
                return R.drawable.bg_stormy;
            case "snow":
            case "mist":
            case "fog":
            case "haze":
                return R.drawable.bg_cloudy;
            default:
                return R.drawable.bg_sunny;
        }
    }
    
    /**
     * Check if current time is night based on sunrise and sunset
     * @param currentTime Current time in seconds
     * @param sunrise Sunrise time in seconds
     * @param sunset Sunset time in seconds
     * @return true if night time
     */
    public static boolean isNightTime(long currentTime, long sunrise, long sunset) {
        return currentTime < sunrise || currentTime > sunset;
    }
    
    /**
     * Format temperature to string
     * @param temp Temperature value
     * @return Formatted temperature string
     */
    public static String formatTemperature(double temp) {
        return String.format("%.0f°", temp);
    }
    
    /**
     * Format humidity to string
     * @param humidity Humidity value
     * @return Formatted humidity string
     */
    public static String formatHumidity(int humidity) {
        return humidity + "%";
    }
    
    /**
     * Format pressure to string
     * @param pressure Pressure value in hPa
     * @return Formatted pressure string
     */
    public static String formatPressure(int pressure) {
        return pressure + " hPa";
    }
    
    /**
     * Format wind speed to string
     * @param speed Wind speed in m/s
     * @return Formatted wind speed string
     */
    public static String formatWindSpeed(double speed) {
        return String.format("%.1f m/s", speed);
    }
}
