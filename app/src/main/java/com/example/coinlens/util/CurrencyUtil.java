package com.example.coinlens.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    public static String formatCurrency(double amount, String currencyCode) {
        Locale locale = Locale.US;
        if ("IDR".equalsIgnoreCase(currencyCode)) {
            locale = new Locale("id", "ID");
        } else if ("EUR".equalsIgnoreCase(currencyCode)) {
            locale = Locale.GERMANY;
        } else if ("JPY".equalsIgnoreCase(currencyCode)) {
            locale = Locale.JAPAN;
        } else if ("GBP".equalsIgnoreCase(currencyCode)) {
            locale = Locale.UK;
        }
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(amount);
    }
}
