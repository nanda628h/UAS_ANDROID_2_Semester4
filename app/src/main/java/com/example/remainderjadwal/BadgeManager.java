package com.example.remainderjadwal;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class BadgeManager {

    private static final String PREF = "badge_pref";
    private static final String KEY_BADGES = "badges";
    private static final String KEY_QUIZ_COUNT = "quiz_count";
    private static final String KEY_STREAK = "streak_count";
    private static final String KEY_CONSEC_CORRECT = "consec_correct";

    public static ArrayList<Badge> getAllBadges(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = p.getString(KEY_BADGES, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Badge>>(){}.getType();
            ArrayList<Badge> list = new Gson().fromJson(json, type);
            if (list != null) return list;
        }
        return getDefaultBadges();
    }

    public static void saveBadges(Context c, ArrayList<Badge> badges) {
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putString(KEY_BADGES, new Gson().toJson(badges)).apply();
    }

    private static ArrayList<Badge> getDefaultBadges() {
        ArrayList<Badge> list = new ArrayList<>();
        // Kuis
        list.add(new Badge("perfect_score",   "Skor Sempurna",   "Raih nilai 100% dalam satu kuis",               "⭐"));
        list.add(new Badge("on_fire",         "On Fire",         "Skor >80% dalam 3 kuis berturut-turut",         "🔥"));
        list.add(new Badge("quiz_maniac",     "Quiz Maniac",     "Selesaikan 10 kuis",                            "📚"));
        list.add(new Badge("quiz_master",     "Quiz Master",     "Selesaikan 50 kuis",                            "👑"));
        list.add(new Badge("speed_demon",     "Speed Demon",     "Selesaikan kuis menggunakan timer",             "⏱️"));
        list.add(new Badge("sharpshooter",    "Sharpshooter",    "Jawab 10 soal benar berturut-turut",            "🎯"));
        // Belajar
        list.add(new Badge("pemula",          "Pemula",          "Buat kuis pertamamu",                           "🌱"));
        list.add(new Badge("ai_explorer",     "AI Explorer",     "Buat kuis menggunakan AI untuk pertama kali",   "🤖"));
        list.add(new Badge("arsitek",         "Arsitek",         "Buat 5 kuis secara manual",                     "🏗️"));
        list.add(new Badge("kolektor",        "Kolektor",        "Miliki 10 kuis tersimpan",                      "📖"));
        // Museum
        list.add(new Badge("hall_of_shame",   "Hall of Shame",   "Kumpulkan 10 kesalahan di Museum",              "💀"));
        list.add(new Badge("reflektif",       "Reflektif",       "Isi alasan di 5 kesalahan Museum",              "✍️"));
        list.add(new Badge("clean_slate",     "Clean Slate",     "Hapus semua kesalahan dari Museum",             "🗑️"));
        // Jadwal
        list.add(new Badge("taat_jadwal",     "Taat Jadwal",     "Tambahkan 5 jadwal kuliah",                     "📅"));
        list.add(new Badge("penjelajah",      "Penjelajah",      "Tambahkan 10 jadwal kuliah",                    "🗺️"));
        // Rahasia
        list.add(new Badge("arkeolog",        "Arkeolog",        "Temukan Museum Kesalahan yang tersembunyi",     "🏛️"));
        list.add(new Badge("seniman",         "Seniman",         "Ganti warna tema aplikasi",                     "🎨"));
        list.add(new Badge("night_owl",       "Night Owl",       "Buka aplikasi antara jam 00.00 - 04.00",        "🌙"));
        return list;
    }

    // Unlock badge by ID dan tampilkan toast
    public static boolean unlock(Context c, String badgeId) {
        ArrayList<Badge> badges = getAllBadges(c);
        for (Badge b : badges) {
            if (b.getId().equals(badgeId) && !b.isUnlocked()) {
                b.unlock();
                saveBadges(c, badges);
                Toast.makeText(c, "🏆 Badge unlocked: " + b.getName(), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public static boolean isUnlocked(Context c, String badgeId) {
        for (Badge b : getAllBadges(c)) {
            if (b.getId().equals(badgeId)) return b.isUnlocked();
        }
        return false;
    }

    // ===== CHECK METHODS =====

    public static void checkQuizCompleted(Context c, int score, boolean usedTimer) {
        // Hitung total kuis
        SharedPreferences p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        int count = p.getInt(KEY_QUIZ_COUNT, 0) + 1;
        p.edit().putInt(KEY_QUIZ_COUNT, count).apply();

        // Skor sempurna
        if (score == 100) unlock(c, "perfect_score");

        // Speed demon
        if (usedTimer) unlock(c, "speed_demon");

        // Quiz maniac & master
        if (count >= 10) unlock(c, "quiz_maniac");
        if (count >= 50) unlock(c, "quiz_master");

        // On fire: streak >80%
        int streak = p.getInt(KEY_STREAK, 0);
        if (score > 80) {
            streak++;
            p.edit().putInt(KEY_STREAK, streak).apply();
            if (streak >= 3) unlock(c, "on_fire");
        } else {
            p.edit().putInt(KEY_STREAK, 0).apply();
        }
    }

    public static void checkConsecCorrect(Context c, int correctInARow) {
        if (correctInARow >= 10) unlock(c, "sharpshooter");
    }

    public static void checkQuizCreated(Context c, boolean isAI) {
        SharedPreferences p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        // Pemula: kuis pertama
        unlock(c, "pemula");

        // AI Explorer
        if (isAI) unlock(c, "ai_explorer");

        // Arsitek: 5 kuis manual
        if (!isAI) {
            int manualCount = p.getInt("manual_quiz_count", 0) + 1;
            p.edit().putInt("manual_quiz_count", manualCount).apply();
            if (manualCount >= 5) unlock(c, "arsitek");
        }
    }

    public static void checkQuizCount(Context c, int totalQuizzes) {
        if (totalQuizzes >= 10) unlock(c, "kolektor");
    }

    public static void checkMuseumWrongCount(Context c, int wrongCount) {
        if (wrongCount >= 10) unlock(c, "hall_of_shame");
    }

    public static void checkMuseumReasonFilled(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        int count = p.getInt("reason_count", 0) + 1;
        p.edit().putInt("reason_count", count).apply();
        if (count >= 5) unlock(c, "reflektif");
    }

    public static void checkMuseumCleared(Context c) {
        unlock(c, "clean_slate");
    }

    public static void checkScheduleCount(Context c, int totalSchedules) {
        if (totalSchedules >= 5)  unlock(c, "taat_jadwal");
        if (totalSchedules >= 10) unlock(c, "penjelajah");
    }

    public static void checkMuseumFound(Context c) {
        unlock(c, "arkeolog");
    }

    public static void checkThemeChanged(Context c) {
        unlock(c, "seniman");
    }

    public static void checkNightOwl(Context c) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 4) unlock(c, "night_owl");
    }
}