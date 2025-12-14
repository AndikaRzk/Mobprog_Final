package com.example.project_mobrog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mobrog.adapter.FavoriteCityAdapter;
import com.example.project_mobrog.model.FavoriteCity;
import com.example.project_mobrog.utils.DatabaseHelper;

import java.util.List;

public class FavoriteCityActivity extends AppCompatActivity {

    public static final String EXTRA_CITY_NAME = "extra_city_name";

    private RecyclerView recyclerView;
    private TextView textEmpty;
    private FavoriteCityAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_city);

        initViews();
        setupRecyclerView();
        
        databaseHelper = new DatabaseHelper(this);
        loadFavorites();

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        textEmpty = findViewById(R.id.textEmpty);
    }

    private void setupRecyclerView() {
        adapter = new FavoriteCityAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(city -> {
            // Return selected city to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_CITY_NAME, city.getCityName());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        adapter.setOnDeleteClickListener(city -> {
            databaseHelper.removeFavorite(city.getCityName());
            loadFavorites();
            Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadFavorites() {
        List<FavoriteCity> favorites = databaseHelper.getAllFavorites();
        if (favorites.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
            adapter.setFavoriteList(favorites);
        }
    }
}
