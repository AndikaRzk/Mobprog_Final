package com.example.project_mobrog.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.project_mobrog.model.FavoriteCity;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "WeatherDb";
    private static final String TABLE_FAVORITES = "favorites";
    
    private static final String KEY_ID = "id";
    private static final String KEY_CITY_NAME = "city_name";
    private static final String KEY_COUNTRY = "country";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CITY_NAME + " TEXT,"
                + KEY_COUNTRY + " TEXT" + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    public void addFavorite(FavoriteCity city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CITY_NAME, city.getCityName());
        values.put(KEY_COUNTRY, city.getCountry());

        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    public void removeFavorite(String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, KEY_CITY_NAME + " = ?",
                new String[] { String.valueOf(cityName) });
        db.close();
    }

    public boolean isFavorite(String cityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_CITY_NAME + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{cityName});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public List<FavoriteCity> getAllFavorites() {
        List<FavoriteCity> favoriteList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FavoriteCity city = new FavoriteCity();
                city.setId(cursor.getInt(0));
                city.setCityName(cursor.getString(1));
                city.setCountry(cursor.getString(2));
                favoriteList.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteList;
    }
}
