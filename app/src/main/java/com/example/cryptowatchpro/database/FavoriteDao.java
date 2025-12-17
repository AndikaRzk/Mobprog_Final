package com.example.cryptowatchpro.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cryptowatchpro.model.Coin;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDao {
    private DatabaseHelper dbHelper;

    public FavoriteDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean addToFavorite(Coin coin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_ID, coin.getId());
        values.put(DatabaseHelper.COL_NAME, coin.getName());
        values.put(DatabaseHelper.COL_SYMBOL, coin.getSymbol());
        values.put(DatabaseHelper.COL_PRICE, coin.getCurrentPrice());
        values.put("currency", "USD"); // Context dependent, keeping simple
        values.put(DatabaseHelper.COL_IMAGE, coin.getImage());
        
        // Save detailed stats
        values.put(DatabaseHelper.COL_MARKET_CAP, coin.getMarketCap());
        values.put(DatabaseHelper.COL_VOLUME, coin.getTotalVolume());
        values.put(DatabaseHelper.COL_HIGH_24H, coin.getHigh24h());
        values.put(DatabaseHelper.COL_LOW_24H, coin.getLow24h());
        values.put(DatabaseHelper.COL_CHANGE_24H, coin.getPriceChangePercentage24h());

        long result = db.insertWithOnConflict(DatabaseHelper.TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result != -1;
    }

    public boolean removeFromFavorite(String coinId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COL_ID + "=?", new String[]{coinId});
        db.close();
        return rows > 0;
    }

    public boolean isFavorite(String coinId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES, null, DatabaseHelper.COL_ID + "=?", new String[]{coinId}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public List<Coin> getAllFavorites() {
        List<Coin> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Coin coin = new Coin();
                coin.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
                coin.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
                coin.setSymbol(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SYMBOL)));
                coin.setCurrentPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRICE)));
                coin.setImage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IMAGE)));
                list.add(coin);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    public Coin getFavoriteCoin(String coinId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_FAVORITES, null, DatabaseHelper.COL_ID + "=?", new String[]{coinId}, null, null, null);
        
        Coin coin = null;
        if (cursor.moveToFirst()) {
            coin = new Coin();
            coin.setId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
            coin.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
            coin.setSymbol(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SYMBOL)));
            coin.setCurrentPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRICE)));
            coin.setImage(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_IMAGE)));
            
            // Retrieve details
            coin.setMarketCap(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MARKET_CAP)));
            coin.setTotalVolume(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_VOLUME)));
            coin.setHigh24h(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HIGH_24H)));
            coin.setLow24h(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LOW_24H)));
            coin.setPriceChangePercentage24h(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CHANGE_24H)));
        }
        cursor.close();
        db.close();
        return coin;
    }
}
