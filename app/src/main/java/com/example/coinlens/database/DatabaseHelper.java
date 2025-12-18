package com.example.coinlens.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "coinlens.db";
    private static final int DATABASE_VERSION = 3; // Incremented for multi-currency support

    public static final String TABLE_FAVORITES = "favorite_coins";
    public static final String COL_ID = "coin_id";
    public static final String COL_NAME = "name";
    public static final String COL_SYMBOL = "symbol";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE = "image_url";
    
    // New Columns for Offline Detail
    public static final String COL_MARKET_CAP = "market_cap";
    public static final String COL_VOLUME = "volume";
    public static final String COL_HIGH_24H = "high_24h";
    public static final String COL_LOW_24H = "low_24h";
    public static final String COL_CHANGE_24H = "price_change_24h";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_FAVORITES + " (" +
                COL_ID + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_SYMBOL + " TEXT, " +
                COL_PRICE + " REAL, " +
                "currency TEXT, " +
                COL_IMAGE + " TEXT, " +
                COL_MARKET_CAP + " REAL, " +
                COL_VOLUME + " REAL, " +
                COL_HIGH_24H + " REAL, " +
                COL_LOW_24H + " REAL, " +
                COL_CHANGE_24H + " REAL, " +
                "PRIMARY KEY (" + COL_ID + ", currency))";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }
}
