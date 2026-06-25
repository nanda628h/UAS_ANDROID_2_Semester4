package com.example.remainderjadwal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LearningCalendarActivity extends AppCompatActivity {

    private TextView tvMonthYear, tvStreakCount, tvTotalDays, tvBestStreak;
    private GridLayout gridCalendar;
    private SharedPreferences prefs;

    private Calendar currentCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_calendar);

        prefs = getSharedPreferences("learning_calendar", MODE_PRIVATE);
        currentCalendar = Calendar.getInstance();

        tvMonthYear   = findViewById(R.id.tvMonthYear);
        tvStreakCount = findViewById(R.id.tvStreakCount);
        tvTotalDays   = findViewById(R.id.tvTotalDays);
        tvBestStreak  = findViewById(R.id.tvBestStreak);
        gridCalendar  = findViewById(R.id.gridCalendar);

        ImageButton btnPrevMonth = findViewById(R.id.btnPrevMonth);
        ImageButton btnNextMonth = findViewById(R.id.btnNextMonth);
        ImageButton btnBack      = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            renderCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            renderCalendar();
        });

        // Tandai hari ini sebagai aktif belajar
        markTodayAsLearned();
        renderCalendar();
        updateStats();
    }

    private void markTodayAsLearned() {
        String today = getTodayKey();
        Set<String> activeDays = new HashSet<>(prefs.getStringSet("active_days", new HashSet<>()));
        if (!activeDays.contains(today)) {
            activeDays.add(today);
            prefs.edit().putStringSet("active_days", activeDays).apply();
        }
    }

    private String getTodayKey() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
    }

    private void renderCalendar() {
        gridCalendar.removeAllViews();

        String[] dayNames = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
        for (String day : dayNames) {
            TextView tv = new TextView(this);
            tv.setText(day);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(12f);
            tv.setTextColor(0xFF1E3A8A);
            tv.setPadding(0, 8, 0, 8);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width  = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);
            gridCalendar.addView(tv);
        }

        Set<String> activeDays = prefs.getStringSet("active_days", new HashSet<>());

        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth    = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayDay       = -1;

        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {
            todayDay = today.get(Calendar.DAY_OF_MONTH);
        }

        // Update header
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("id"));
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));

        // Empty cells sebelum hari pertama
        for (int i = 0; i < firstDayOfWeek; i++) {
            TextView empty = new TextView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width  = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            empty.setLayoutParams(params);
            gridCalendar.addView(empty);
        }

        // Render tiap hari
        for (int day = 1; day <= daysInMonth; day++) {
            String dateKey = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH) + 1,
                    day);

            boolean isActive = activeDays.contains(dateKey);
            boolean isToday  = (day == todayDay);

            CardView card = new CardView(this);
            card.setRadius(20f);
            card.setCardElevation(isToday ? 6f : 0f);

            if (isToday) {
                card.setCardBackgroundColor(0xFF1E88E5);
            } else if (isActive) {
                card.setCardBackgroundColor(0xFF4CAF50);
            } else {
                card.setCardBackgroundColor(0xFFF0F4FF);
            }

            TextView tvDay = new TextView(this);
            tvDay.setText(String.valueOf(day));
            tvDay.setGravity(Gravity.CENTER);
            tvDay.setTextSize(13f);
            tvDay.setPadding(0, 16, 0, 16);

            if (isToday || isActive) {
                tvDay.setTextColor(0xFFFFFFFF);
                tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                tvDay.setTextColor(0xFF555555);
            }

            card.addView(tvDay);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width  = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(4, 4, 4, 4);
            card.setLayoutParams(params);

            int finalDay = day;
            card.setOnClickListener(v -> {
                Toast.makeText(this,
                        isActive ? "✅ Kamu belajar di hari ini!" : "📅 Belum ada aktivitas",
                        Toast.LENGTH_SHORT).show();
            });

            gridCalendar.addView(card);
        }
    }

    private void updateStats() {
        Set<String> activeDays = prefs.getStringSet("active_days", new HashSet<>());
        int totalDays = activeDays.size();
        tvTotalDays.setText(String.valueOf(totalDays));

        // Hitung streak sekarang
        int streak = 0;
        Calendar c = Calendar.getInstance();
        while (true) {
            String key = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.getTime());
            if (activeDays.contains(key)) {
                streak++;
                c.add(Calendar.DAY_OF_MONTH, -1);
            } else break;
        }
        tvStreakCount.setText(streak + " 🔥");

        // Hitung best streak
        int bestStreak = prefs.getInt("best_streak", 0);
        if (streak > bestStreak) {
            bestStreak = streak;
            prefs.edit().putInt("best_streak", bestStreak).apply();
        }
        tvBestStreak.setText(String.valueOf(bestStreak));
    }
}