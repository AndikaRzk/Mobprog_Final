package com.example.project_mobrog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mobrog.R;
import com.example.project_mobrog.api.ApiClient;
import com.example.project_mobrog.model.ForecastResponse;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for 5-day forecast
 * Uses Picasso to load weather icons
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    
    private List<ForecastResponse.ForecastItem> forecastList = new ArrayList<>();
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("id", "ID"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", new Locale("id", "ID"));
    
    public void setForecastList(List<ForecastResponse.ForecastItem> list) {
        this.forecastList = list;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastResponse.ForecastItem item = forecastList.get(position);
        
        // Format date
        Date date = new Date(item.getDt() * 1000);
        holder.textDay.setText(dayFormat.format(date));
        holder.textDate.setText(dateFormat.format(date));
        
        // Temperature
        holder.textTempHigh.setText(String.format(Locale.getDefault(), "%.0f°", item.getMain().getTempMax()));
        holder.textTempLow.setText(String.format(Locale.getDefault(), "%.0f°", item.getMain().getTempMin()));
        
        // Weather description
        if (item.getWeather() != null && !item.getWeather().isEmpty()) {
            String description = item.getWeather().get(0).getDescription();
            // Capitalize first letter
            description = description.substring(0, 1).toUpperCase() + description.substring(1);
            holder.textDescription.setText(description);
            
            // Load weather icon using Picasso
            String iconCode = item.getWeather().get(0).getIcon();
            String iconUrl = ApiClient.getIconUrl(iconCode);
            Picasso.get()
                    .load(iconUrl)
                    .into(holder.imageWeatherIcon);
        }
    }
    
    @Override
    public int getItemCount() {
        return forecastList.size();
    }
    
    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView textDay;
        TextView textDate;
        ImageView imageWeatherIcon;
        TextView textTempHigh;
        TextView textTempLow;
        TextView textDescription;
        
        ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.textDay);
            textDate = itemView.findViewById(R.id.textDate);
            imageWeatherIcon = itemView.findViewById(R.id.imageWeatherIcon);
            textTempHigh = itemView.findViewById(R.id.textTempHigh);
            textTempLow = itemView.findViewById(R.id.textTempLow);
            textDescription = itemView.findViewById(R.id.textDescription);
        }
    }
}
