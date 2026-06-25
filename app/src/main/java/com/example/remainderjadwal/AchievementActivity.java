package com.example.remainderjadwal;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AchievementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        RecyclerView rv              = findViewById(R.id.rvBadges);
        TextView tvCount             = findViewById(R.id.tvAchievementCount);
        ProgressBar progressBar      = findViewById(R.id.progressAchievement);
        MaterialButton btnBack       = findViewById(R.id.btnBack);

        ArrayList<Badge> badges = BadgeManager.getAllBadges(this);

        // Hitung yang sudah terbuka
        int unlocked = 0;
        for (Badge b : badges) {
            if (b.isUnlocked()) unlocked++;
        }

        tvCount.setText(unlocked + " / " + badges.size() + " badge terbuka");
        progressBar.setMax(badges.size());
        progressBar.setProgress(unlocked);

        // Urutkan: yang terbuka di atas
        badges.sort((a, b) -> Boolean.compare(!a.isUnlocked(), !b.isUnlocked()));

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new BadgeAdapter(this, badges));

        btnBack.setOnClickListener(v -> finish());
    }
}