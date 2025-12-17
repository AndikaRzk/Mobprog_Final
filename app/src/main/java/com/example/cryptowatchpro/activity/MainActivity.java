package com.example.cryptowatchpro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptowatchpro.R;
import com.example.cryptowatchpro.adapter.CoinAdapter;
import com.example.cryptowatchpro.model.Coin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CoinAdapter adapter;
    private ProgressBar progressBar;
    private Spinner currencySpinner;
    private SwipeRefreshLayout swipeRefresh;
    private EditText etSearch;
    private ImageButton btnFavorite;

    private String selectedCurrency = "usd";
    private RequestQueue requestQueue;
    private List<Coin> coinList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        recyclerView = findViewById(R.id.recyclerViewCoins);
        progressBar = findViewById(R.id.progressBar);
        currencySpinner = findViewById(R.id.currencySpinner);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        etSearch = findViewById(R.id.etSearch);
        btnFavorite = findViewById(R.id.btnFavorite);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Setup Spinner
        String[] currencies = {"USD", "IDR", "EUR", "JPY", "GBP"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(spinnerAdapter);

        requestQueue = Volley.newRequestQueue(this);

        adapter = new CoinAdapter(coinList, selectedCurrency, coin -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("coin_id", coin.getId());
            intent.putExtra("currency", selectedCurrency);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        setupListeners();
        fetchCoinData();
    }

    private void setupListeners() {
        // Currency Spinner
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newCurrency = parent.getItemAtPosition(position).toString().toLowerCase();
                if (!newCurrency.equals(selectedCurrency)) {
                    selectedCurrency = newCurrency;
                    fetchCoinData();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Swipe Refresh
        swipeRefresh.setOnRefreshListener(this::fetchCoinData);

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Favorite Button
        btnFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });
    }

    private void fetchCoinData() {
        if (!swipeRefresh.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=" + selectedCurrency;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Coin>>() {}.getType();
                        List<Coin> newOpenList = gson.fromJson(response, listType);
                        
                        coinList.clear();
                        coinList.addAll(newOpenList);
                        adapter.updateCurrency(selectedCurrency.toUpperCase());
                        adapter.updateData(coinList);
                        
                        // Re-filter if search text exists
                        if (etSearch.getText().length() > 0) {
                            adapter.filter(etSearch.getText().toString());
                        }

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(stringRequest);
    }
}
