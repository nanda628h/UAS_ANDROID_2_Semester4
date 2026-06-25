package com.example.remainderjadwal;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MuseumStorage {
    private static final String PREF = "museum_pref";
    private static final String KEY = "wrong_answers";

    public static ArrayList<WrongAnswer> getWrongAnswers(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = p.getString(KEY, null);
        Type type = new TypeToken<ArrayList<WrongAnswer>>(){}.getType();
        ArrayList<WrongAnswer> list = new Gson().fromJson(json, type);
        if (list == null) list = new ArrayList<>();
        return list;
    }

    public static void saveWrongAnswers(Context c, ArrayList<WrongAnswer> list) {
        SharedPreferences p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        p.edit().putString(KEY, new Gson().toJson(list)).apply();
    }

    public static void addWrongAnswer(Context c, WrongAnswer wa) {
        ArrayList<WrongAnswer> list = getWrongAnswers(c);
        list.add(wa);
        saveWrongAnswers(c, list);
    }
}