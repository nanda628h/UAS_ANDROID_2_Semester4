package com.example.remainderjadwal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Schedule> list = new ArrayList<>();
    private ScheduleAdapter adapter;
    private int tapCount = 0;
    private long lastTapTime = 0;

    private ImageButton btnProfile;
    private TextView tvNamaProfil, tvJurusanProfil;

    private LinearLayout btnQuiz;
    private LinearLayout btnStatistik;
    private FloatingActionButton btnAdd;
    private ImageButton btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BadgeManager.checkNightOwl(this);

        RecyclerView rv          = findViewById(R.id.rvSchedule);
        btnAdd                   = findViewById(R.id.btnAdd);
        btnSettings              = findViewById(R.id.btnTheme);
        btnQuiz                  = findViewById(R.id.btnQuiz);
        LinearLayout btnCalendar = findViewById(R.id.btnCalendar);
        btnStatistik             = findViewById(R.id.btnStatistik);
        TextClock tcTime         = findViewById(R.id.tcTime);
        btnProfile               = findViewById(R.id.btnProfile);
        tvNamaProfil             = findViewById(R.id.tvNamaProfil);
        tvJurusanProfil          = findViewById(R.id.tvJurusanProfil);

        adapter = new ScheduleAdapter(this, list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddScheduleActivity.class)));

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        btnQuiz.setOnClickListener(v ->
                startActivity(new Intent(this, QuizListActivity.class)));

        btnCalendar.setOnClickListener(v ->
                startActivity(new Intent(this, LearningCalendarActivity.class)));

        btnStatistik.setOnClickListener(v ->
                startActivity(new Intent(this, StatistikActivity.class)));

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        // Easter egg
        tcTime.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if (now - lastTapTime > 3000) tapCount = 0;
            lastTapTime = now;
            tapCount++;

            if (tapCount == 3) {
                Toast.makeText(this, "🏛️ Hampir...", Toast.LENGTH_SHORT).show();
            } else if (tapCount >= 5) {
                tapCount = 0;
                BadgeManager.checkMuseumFound(this);
                Toast.makeText(this, "🏛️ Selamat datang di Museum Kesalahan!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MuseumActivity.class));
            }
        });

        btnQuiz.post(() -> showTutorial());
    }

    private void showTutorial() {
        if (!TutorialManager.isFirstTime(this)) return;

        ViewGroup root = (ViewGroup) getWindow().getDecorView();

        TutorialOverlayView overlay = new TutorialOverlayView(this);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        String[] pesan = {
                "Hei! Ini tombol Quiz\nUntuk mulai atau buat kuis!",
                "Ini tombol tambah jadwal\nTekan untuk tambah jadwal kuliah!",
                "Ini tombol pengaturan\nUntuk ganti tema & reminder!",
                "Ini tombol profil\nAtur data dan foto profilmu!"
        };

        View[] targets = {btnQuiz, btnAdd, btnSettings, btnProfile};
        final int[] step = {0};

        root.addView(overlay);

        // Karakter maskot
        ImageView mascot = overlay.createMascotView(this);
        FrameLayout.LayoutParams lpMascot = new FrameLayout.LayoutParams(500, 500);
        lpMascot.gravity = Gravity.NO_GRAVITY;
        mascot.setLayoutParams(lpMascot);
        root.addView(mascot);
        overlay.startTalking();

        // TextView penjelasan
        TextView tvHint = new TextView(this);
        tvHint.setTextColor(Color.WHITE);
        tvHint.setTextSize(17);
        tvHint.setGravity(Gravity.CENTER);
        tvHint.setPadding(48, 0, 48, 0);
        FrameLayout.LayoutParams lpHint = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lpHint.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lpHint.bottomMargin = 330;
        tvHint.setLayoutParams(lpHint);
        root.addView(tvHint);

        // Tombol next
        Button btnNext = new Button(this);
        btnNext.setText("Selanjutnya →");
        btnNext.setTextColor(Color.WHITE);
        btnNext.setBackgroundTintList(ColorStateList.valueOf(
                ThemeManager.getAccentColor(this)));
        FrameLayout.LayoutParams lpBtn = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lpBtn.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        lpBtn.bottomMargin = 120;
        btnNext.setLayoutParams(lpBtn);
        root.addView(btnNext);

        Runnable showStep = new Runnable() {
            @Override
            public void run() {
                View target = targets[step[0]];
                int[] loc = new int[2];
                target.getLocationOnScreen(loc);
                float cx = loc[0] + target.getWidth() / 2f;
                float cy = loc[1] + target.getHeight() / 2f;
                float radius = Math.max(target.getWidth(), target.getHeight()) * 0.9f;
                overlay.setHole(cx, cy, radius);
                tvHint.setText(pesan[step[0]]);

                // Maskot ikut posisi spotlight
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mascot.getLayoutParams();
                lp.gravity = Gravity.NO_GRAVITY;
                int screenHeight = getWindow().getDecorView().getHeight();
                int screenWidth = getWindow().getDecorView().getWidth();

                // Taruh maskot di bawah spotlight kalau ada ruang, kalau tidak di atas
                if (cy + radius + 520 < screenHeight) {
                    lp.topMargin = (int)(cy + radius + 10);
                } else {
                    lp.topMargin = (int)(cy - radius - 520);
                }

                // Jaga biar tidak keluar layar kiri/kanan
                int leftMargin = (int)(cx - 250);
                if (leftMargin < 0) leftMargin = 0;
                if (leftMargin + 500 > screenWidth) leftMargin = screenWidth - 500;
                lp.leftMargin = leftMargin;

                mascot.setLayoutParams(lp);

                if (step[0] == pesan.length - 1) {
                    btnNext.setText("Mulai! 🚀");
                }
            }
        };

        overlay.post(showStep);

        btnNext.setOnClickListener(v -> {
            step[0]++;
            if (step[0] >= targets.length) {
                overlay.stopTalking();
                root.removeView(mascot);
                root.removeView(overlay);
                root.removeView(tvHint);
                root.removeView(btnNext);
                TutorialManager.markDone(this);
            } else {
                showStep.run();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
        loadProfil();
        applyTheme();
    }

    private void applyTheme() {
        int color = ThemeManager.getAccentColor(this);
        ColorStateList csl = ColorStateList.valueOf(color);
        btnAdd.setBackgroundTintList(csl);
        btnQuiz.setBackgroundTintList(csl);
    }

    private void loadProfil() {
        SharedPreferences prefs = getSharedPreferences("profile_pref", MODE_PRIVATE);
        String nama      = prefs.getString("nama", "");
        String jurusan   = prefs.getString("jurusan", "");
        String photoPath = prefs.getString("photo_path", null);

        tvNamaProfil.setText(nama.isEmpty() ? "Halo, Pengguna! 👋" : "Halo, " + nama + "! 👋");
        tvJurusanProfil.setText(jurusan.isEmpty() ? "Atur profil kamu" : jurusan);

        if (photoPath != null) {
            File f = new File(photoPath);
            if (f.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(photoPath);
                btnProfile.setImageBitmap(bm);
            }
        }
    }

    private void refreshData() {
        ArrayList<Schedule> updatedList = ScheduleStorage.getSchedules(this);
        if (updatedList != null) {
            list.clear();
            list.addAll(updatedList);
            if (adapter != null) adapter.notifyDataSetChanged();
        }
    }
}