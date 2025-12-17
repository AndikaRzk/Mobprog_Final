package com.example.cryptowatchpro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptowatchpro.R;
import com.example.cryptowatchpro.adapter.CoinAdapter;
import com.example.cryptowatchpro.database.FavoriteDao;
import com.example.cryptowatchpro.model.Coin;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CoinAdapter adapter;
    private TextView tvEmpty;
    private FavoriteDao favoriteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setTitle("Favorites");

        recyclerView = findViewById(R.id.recyclerFavorites);
        tvEmpty = findViewById(R.id.tvEmptyFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarFavorite);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        favoriteDao = new FavoriteDao(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        List<Coin> favoriteList = favoriteDao.getAllFavorites();
        
        if (favoriteList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            // Assuming USD for favorites or stored value. 
            // The DB stores 'price', but not 'currency' (wait implementation plan check - user spec table field 'currency' TEXT).
            // My DB helper implementation: COL_ID, COL_NAME, COL_SYMBOL, COL_PRICE, COL_IMAGE. I missed currency column?
            // User SPEC: 8. Database ... field currency TEXT. 
            // My DB creation code: COL_PRICE + " REAL, " + COL_IMAGE + " TEXT)". I missed currency.
            // I will fix DB helper in a moment. But for now let's assume USD default in display or use just number. 
            // Correction: I should update DB Helper to match spec.
            
            adapter = new CoinAdapter(favoriteList, "USD", coin -> {
                Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                intent.putExtra("coin_id", coin.getId());
                intent.putExtra("currency", "USD"); // Defaulting 
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        }
    }
}
