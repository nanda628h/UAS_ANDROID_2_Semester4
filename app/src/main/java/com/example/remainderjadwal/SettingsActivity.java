package com.example.remainderjadwal;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    private View viewColorPreview;
    private TextInputEditText etHexColor;
    private MaterialButton btnApplyColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        viewColorPreview = findViewById(R.id.viewColorPreview);
        etHexColor       = findViewById(R.id.etHexColor);
        btnApplyColor    = findViewById(R.id.btnApplyColor);

        MaterialButton btnBack = findViewById(R.id.btnBack);

        // Load warna saat ini
        String currentHex = ThemeManager.getAccentColorHex(this);
        etHexColor.setText(currentHex);
        updatePreview(currentHex);
        updateButtonColor(currentHex);

        // Preview live saat ketik
        etHexColor.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String hex = s.toString().trim();
                if (!hex.startsWith("#")) hex = "#" + hex;
                if (ThemeManager.isValidHex(hex)) {
                    updatePreview(hex);
                }
            }
        });

        // achivment
        findViewById(R.id.btnAchievement).setOnClickListener(v ->
                startActivity(new Intent(this, AchievementActivity.class)));

        // Apply warna
        btnApplyColor.setOnClickListener(v -> {
            String hex = etHexColor.getText().toString().trim();
            if (!hex.startsWith("#")) hex = "#" + hex;

            if (ThemeManager.isValidHex(hex)) {
                ThemeManager.saveAccentColor(this, hex);
                updateButtonColor(hex);
                Toast.makeText(this, "✅ Warna diterapkan!", Toast.LENGTH_SHORT).show();
                BadgeManager.checkThemeChanged(this);
            } else {
                Toast.makeText(this, "❌ Kode hex tidak valid! Contoh: #1E40AF", Toast.LENGTH_SHORT).show();
            }
        });

        // Preset warna
        setupPreset(R.id.presetBlue,   "#1E40AF");
        setupPreset(R.id.presetPurple, "#7C3AED");
        setupPreset(R.id.presetGreen,  "#059669");
        setupPreset(R.id.presetRed,    "#DC2626");
        setupPreset(R.id.presetOrange, "#EA580C");
        setupPreset(R.id.presetPink,   "#DB2777");
        setupPreset(R.id.presetTeal,   "#0891B2");
        setupPreset(R.id.presetYellow, "#D97706");

        btnBack.setOnClickListener(v -> finish());

        // Reminder
        android.widget.Button btnReminder = findViewById(R.id.btnSetReminder);
        if (btnReminder != null) {
            // Tampilkan jam yang tersimpan
            if (ReminderManager.isEnabled(this)) {
                int h = ReminderManager.getSavedHour(this);
                int m = ReminderManager.getSavedMinute(this);
                btnReminder.setText(String.format("⏰ Pengingat: %02d:%02d", h, m));
            }

            btnReminder.setOnClickListener(v -> {
                int savedHour = ReminderManager.getSavedHour(this);
                int savedMinute = ReminderManager.getSavedMinute(this);

                android.app.TimePickerDialog tpd = new android.app.TimePickerDialog(this,
                        (view, hour, minute) -> {
                            ReminderManager.setReminder(this, hour, minute);
                            btnReminder.setText(String.format("⏰ Pengingat: %02d:%02d", hour, minute));
                            Toast.makeText(this, "✅ Pengingat diset jam " + String.format("%02d:%02d", hour, minute), Toast.LENGTH_SHORT).show();
                        }, savedHour, savedMinute, true);
                tpd.show();
            });

            // Tombol matikan reminder
            android.widget.Button btnCancelReminder = findViewById(R.id.btnCancelReminder);
            if (btnCancelReminder != null) {
                btnCancelReminder.setOnClickListener(v -> {
                    ReminderManager.cancelReminder(this);
                    btnReminder.setText("⏰ Set Pengingat Belajar");
                    Toast.makeText(this, "🔕 Pengingat dimatikan", Toast.LENGTH_SHORT).show();
                });
            }
        }

    }

    private void setupPreset(int viewId, String hex) {
        findViewById(viewId).setOnClickListener(v -> {
            etHexColor.setText(hex);
            updatePreview(hex);
            ThemeManager.saveAccentColor(this, hex);
            updateButtonColor(hex);
            Toast.makeText(this, "✅ Warna diterapkan!", Toast.LENGTH_SHORT).show();
            BadgeManager.checkThemeChanged(this);
        });
    }

    private void updatePreview(String hex) {
        try {
            viewColorPreview.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor(hex)));
        } catch (Exception ignored) {}
    }

    private void updateButtonColor(String hex) {
        try {
            btnApplyColor.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor(hex)));
        } catch (Exception ignored) {}
    }
}