package com.example.remainderjadwal;

import android.content.Context;
import android.content.SharedPreferences;

public class TutorialManager {

    private static final String PREF_NAME = "tutorial_pref";
    private static final String KEY_DONE = "tutorial_done";

    public static boolean isFirstTime(Context context) {
        return !context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_DONE, false);
    }

    public static void markDone(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_DONE, true)
                .apply();
    }
}