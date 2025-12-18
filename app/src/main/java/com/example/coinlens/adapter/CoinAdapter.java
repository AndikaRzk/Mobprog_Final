package com.example.coinlens.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coinlens.R;
import com.example.coinlens.model.Coin;
import com.example.coinlens.util.CurrencyUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinViewHolder> {

    private List<Coin> coinList;
    private List<Coin> coinListFull; // For search filtering
    private String currencyCode;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Coin coin);
    }

    public CoinAdapter(List<Coin> coinList, String currencyCode, OnItemClickListener listener) {
        this.coinList = coinList;
        this.coinListFull = new ArrayList<>(coinList);
        this.currencyCode = currencyCode;
        this.listener = listener;
    }

    public void updateData(List<Coin> newOpenList) {
        this.coinList = newOpenList;
        this.coinListFull = new ArrayList<>(newOpenList);
        notifyDataSetChanged();
    }
    
    public void updateCurrency(String newCurrency) {
        this.currencyCode = newCurrency;
        notifyDataSetChanged();
    }

    public void filter(String text) {
        coinList.clear();
        if (text.isEmpty()) {
            coinList.addAll(coinListFull);
        } else {
            text = text.toLowerCase();
            for (Coin item : coinListFull) {
                if (item.getName().toLowerCase().contains(text) || item.getSymbol().toLowerCase().contains(text)) {
                    coinList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin, parent, false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        Coin coin = coinList.get(position);
        holder.bind(coin, currencyCode, listener);
    }

    @Override
    public int getItemCount() {
        return coinList.size();
    }

    static class CoinViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLogo;
        TextView tvName, tvSymbol, tvPrice, tvChange;

        public CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLogo = itemView.findViewById(R.id.imgCoinLogo);
            tvName = itemView.findViewById(R.id.tvCoinName);
            tvSymbol = itemView.findViewById(R.id.tvCoinSymbol);
            tvPrice = itemView.findViewById(R.id.tvCoinPrice);
            tvChange = itemView.findViewById(R.id.tvPriceChange);
        }

        public void bind(final Coin coin, String currencyCode, final OnItemClickListener listener) {
            tvName.setText(coin.getName());
            tvSymbol.setText(coin.getSymbol());
            String displayCurrency = coin.getCurrency() != null ? coin.getCurrency() : currencyCode;
            tvPrice.setText(CurrencyUtil.formatCurrency(coin.getCurrentPrice(), displayCurrency));

            double change = coin.getPriceChangePercentage24h();
            tvChange.setText(String.format("%.2f%%", change));
            
            if (change < 0) {
                tvChange.setTextColor(Color.RED);
            } else {
                tvChange.setTextColor(Color.GREEN);
            }

            if (coin.getImage() != null && !coin.getImage().isEmpty()) {
                Picasso.get().load(coin.getImage()).into(imgLogo);
            } else {
                imgLogo.setImageResource(android.R.drawable.ic_menu_help); // Fallback
            }

            itemView.setOnClickListener(v -> listener.onItemClick(coin));
        }
    }
}
