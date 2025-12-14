package com.example.project_mobrog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mobrog.R;
import com.example.project_mobrog.model.FavoriteCity;

import java.util.ArrayList;
import java.util.List;

public class FavoriteCityAdapter extends RecyclerView.Adapter<FavoriteCityAdapter.ViewHolder> {

    private List<FavoriteCity> favoriteList = new ArrayList<>();
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(FavoriteCity city);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(FavoriteCity city);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setFavoriteList(List<FavoriteCity> list) {
        this.favoriteList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteCity city = favoriteList.get(position);
        holder.bind(city);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCityName;
        TextView textCountryName;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCityName = itemView.findViewById(R.id.textCityName);
            textCountryName = itemView.findViewById(R.id.textCountryName);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(favoriteList.get(position));
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (deleteListener != null && position != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(favoriteList.get(position));
                }
            });
        }

        void bind(FavoriteCity city) {
            textCityName.setText(city.getCityName());
            textCountryName.setText(city.getCountry());
        }
    }
}
