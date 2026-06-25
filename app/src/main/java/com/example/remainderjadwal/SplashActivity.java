package com.example.remainderjadwal;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animasi runner lari kiri ke kanan bolak balik
        View runner = findViewById(R.id.viewRunner);
        runner.post(() -> {
            int trackWidth = ((View) runner.getParent()).getWidth();
            int runnerWidth = runner.getWidth();
            float maxX = trackWidth - runnerWidth;

            ObjectAnimator animator = ObjectAnimator.ofFloat(runner, "translationX", 0f, maxX);
            animator.setDuration(1000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 2500);
    }
}