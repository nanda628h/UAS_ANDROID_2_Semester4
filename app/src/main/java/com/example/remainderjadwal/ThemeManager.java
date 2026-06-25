package com.example.remainderjadwal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ThemeManager {

    private static final String PREF = "theme_pref";
    private static final String KEY_COLOR = "accent_color";
    private static final String DEFAULT_COLOR = "#1E40AF"; // biru default

    public static void saveAccentColor(Context c, String hexColor) {
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putString(KEY_COLOR, hexColor).apply();
    }

    public static String getAccentColorHex(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString(KEY_COLOR, DEFAULT_COLOR);
    }

    public static int getAccentColor(Context c) {
        try {
            return Color.parseColor(getAccentColorHex(c));
        } catch (Exception e) {
            return Color.parseColor(DEFAULT_COLOR);
        }
    }

    // Validasi hex color
    public static boolean isValidHex(String hex) {
        try {
            if (!hex.startsWith("#")) hex = "#" + hex;
            Color.parseColor(hex);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}