package com.example.remainderjadwal;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ScoreHistoryStorage {

    private static final String PREF_NAME = "score_history_pref";
    private static final String KEY = "history";

    public static void addHistory(Context context, ScoreHistory history) {
        ArrayList<ScoreHistory> list = getHistory(context);
        list.add(history);
        // Maksimal simpan 30 data terakhir
        if (list.size() > 30) {
            list.remove(0);
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY, new Gson().toJson(list)).apply();
    }

    public static ArrayList<ScoreHistory> getHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<ScoreHistory>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void clearHistory(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().remove(KEY).apply();
    }
}