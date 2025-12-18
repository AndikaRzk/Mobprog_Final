package com.example.cryptowatchpro.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptowatchpro.R;
import com.example.cryptowatchpro.database.FavoriteDao;
import com.example.cryptowatchpro.model.Coin;
import com.example.cryptowatchpro.model.MarketChartResponse;
import com.example.cryptowatchpro.network.ApiClient;
import com.example.cryptowatchpro.util.CurrencyUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private String coinId;
    private String currency; // Default USD
    
    private ImageView imgLogo;
    private TextView tvName, tvSymbol, tvPrice, tvChange;
    private TextView tvMarketCap, tvVolume, tvHigh24h, tvLow24h;
    private LineChart lineChart;
    private FloatingActionButton fabFavorite;
    
    private FavoriteDao favoriteDao;
    private Coin currentCoin;
    private boolean isFavorite = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        coinId = getIntent().getStringExtra("coin_id");
        currency = getIntent().getStringExtra("currency");
        if (currency == null) currency = "usd";

        imgLogo = findViewById(R.id.imgDetailLogo);
        tvName = findViewById(R.id.tvDetailName);
        tvSymbol = findViewById(R.id.tvDetailSymbol);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvChange = findViewById(R.id.tvDetailChange);
        tvMarketCap = findViewById(R.id.tvMarketCap);
        tvVolume = findViewById(R.id.tvVolume);
        tvHigh24h = findViewById(R.id.tvHigh24h);
        tvLow24h = findViewById(R.id.tvLow24h);
        lineChart = findViewById(R.id.lineChart);
        fabFavorite = findViewById(R.id.fabFavorite);
        
        // Setup Toolbar - Transparent/Overlay
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(coinId != null ? coinId.toUpperCase() : "Detail Coin");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        favoriteDao = new FavoriteDao(this);
        
        checkFavoriteStatus();
        loadCoinDetails();
        loadChartData();
        
        fabFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void checkFavoriteStatus() {
        if (coinId != null) {
            isFavorite = favoriteDao.isFavorite(coinId, currency);
            updateFavoriteIcon();
        }
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_heart_filled);
        } else {
            fabFavorite.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    private void toggleFavorite() {
        if (currentCoin == null) return;
        
        if (isFavorite) {
            favoriteDao.removeFromFavorite(coinId, currency);
            isFavorite = false;
            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
        } else {
            currentCoin.setCurrency(currency);
            favoriteDao.addToFavorite(currentCoin);
            isFavorite = true;
            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
        }
        updateFavoriteIcon();
    }

    private void loadCoinDetails() {
        android.util.Log.d("CryptoWatch", "Loading details for: " + coinId + ", currency: " + currency);
        ApiClient.getService().getCoinDetailViaMarkets(currency, coinId).enqueue(new Callback<List<Coin>>() {
            @Override
            public void onResponse(Call<List<Coin>> call, Response<List<Coin>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    currentCoin = response.body().get(0);
                    displayData(currentCoin);
                } else {
                    String errorMsg = "Failed Coingecko: " + response.code();
                    if (response.code() == 429) {
                        tryOfflineLoad("Rate Limit. Showing offline data.");
                    } else {
                        android.util.Log.e("CryptoWatch", errorMsg);
                        Toast.makeText(DetailActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Coin>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Unknown Network Error";
                android.util.Log.e("CryptoWatch", "Failure: " + msg);
                tryOfflineLoad("Network Error. Showing offline data.");
            }
        });
    }

    private void tryOfflineLoad(String reason) {
        Coin offlineCoin = favoriteDao.getFavoriteCoin(coinId, currency);
        if (offlineCoin != null) {
            currentCoin = offlineCoin;
            // If cached coin has a stored currency, prefer it for display consistency
            if (currentCoin.getCurrency() != null) {
                this.currency = currentCoin.getCurrency();
            }
            displayData(currentCoin);
            Toast.makeText(DetailActivity.this, reason, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(DetailActivity.this, reason + " No offline data found.", Toast.LENGTH_LONG).show();
        }
    }

    private void displayData(Coin coin) {
        String displayCurrency = coin.getCurrency() != null ? coin.getCurrency() : currency;

        tvName.setText(coin.getName());
        tvSymbol.setText(coin.getSymbol());
        tvPrice.setText(CurrencyUtil.formatCurrency(coin.getCurrentPrice(), displayCurrency));
        
        double change = coin.getPriceChangePercentage24h();
        tvChange.setText(String.format("%.2f%%", change));
        
        if (change < 0) {
            tvChange.setTextColor(Color.RED);
        } else {
            tvChange.setTextColor(Color.GREEN);
        }
        
        tvMarketCap.setText(getString(R.string.market_cap) + ": " + CurrencyUtil.formatCurrency(coin.getMarketCap(), displayCurrency));
        tvVolume.setText(getString(R.string.volume) + ": " + CurrencyUtil.formatCurrency(coin.getTotalVolume(), displayCurrency));
        tvHigh24h.setText(CurrencyUtil.formatCurrency(coin.getHigh24h(), displayCurrency));
        tvLow24h.setText(CurrencyUtil.formatCurrency(coin.getLow24h(), displayCurrency));

        if (coin.getImage() != null && !coin.getImage().isEmpty()) {
            Picasso.get().load(coin.getImage()).into(imgLogo);
        }
    }

    private void loadChartData() {
        ApiClient.getService().getCoinMarketChart(coinId, currency, 7).enqueue(new Callback<MarketChartResponse>() {
            @Override
            public void onResponse(Call<MarketChartResponse> call, Response<MarketChartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupChart(response.body().getPrices());
                }
            }

            @Override
            public void onFailure(Call<MarketChartResponse> call, Throwable t) {
                // Log error
            }
        });
    }

    private void setupChart(List<List<Double>> prices) {
        if (prices == null || prices.isEmpty()) return;

        List<Entry> entries = new ArrayList<>();
        for (List<Double> point : prices) {
            // point[0] = time, point[1] = price
            entries.add(new Entry(point.get(0).floatValue(), point.get(1).floatValue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Price (7 Days)");
        dataSet.setColor(Color.BLUE);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);
        
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false); // Hide timestamps for simplicity
        
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        
        lineChart.invalidate();
    }
}
