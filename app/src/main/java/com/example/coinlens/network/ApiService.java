package com.example.coinlens.network;

import com.example.coinlens.model.Coin;
import com.example.coinlens.model.MarketChartResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Using markets endpoint to fetch single coin detail to reuse Coin model
    @GET("coins/markets")
    Call<java.util.List<Coin>> getCoinDetailViaMarkets(
            @Query("vs_currency") String currency,
            @Query("ids") String id
    );

    @GET("coins/{id}/market_chart")
    Call<MarketChartResponse> getCoinMarketChart(
            @Path("id") String id,
            @Query("vs_currency") String currency,
            @Query("days") int days
    );
}
