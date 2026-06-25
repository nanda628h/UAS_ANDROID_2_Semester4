package com.example.remainderjadwal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ScheduleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        TextView tvDosen         = findViewById(R.id.tvDosen);
        TextView tvRuangan       = findViewById(R.id.tvRuangan);
        TextView tvJam           = findViewById(R.id.tvJam);
        TextView tvCatatan       = findViewById(R.id.tvCatatan);
        TextView tvWeatherIcon   = findViewById(R.id.tvWeatherIcon);
        TextView tvWeatherStatus = findViewById(R.id.tvWeatherStatus);
        Button btnBack           = findViewById(R.id.btnBack);

        Schedule s = (Schedule) getIntent().getSerializableExtra("schedule");

        if (s != null) {
            tvDosen.setText(s.getDosen());
            tvRuangan.setText(s.getRuangan());
            tvJam.setText(s.getJam());
            String catatan = s.getCatatan();
            tvCatatan.setText(catatan == null || catatan.isEmpty() ? "-" : catatan);

            // Weather logic sama kayak di adapter
            applyWeather(tvWeatherIcon, tvWeatherStatus, s.getJam());
        } else {
            Toast.makeText(this, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void applyWeather(TextView icon, TextView status, String jam) {
        try {
            String[] parts = jam.split(":");
            int hour   = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar now = Calendar.getInstance();
            int nowMinutes      = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
            int scheduleMinutes = hour * 60 + minute;
            int diff            = scheduleMinutes - nowMinutes;

            if (diff < 0) {
                icon.setText("🌙");
                status.setText("Jadwal sudah selesai");
            } else if (diff <= 30) {
                icon.setText("⛈️");
                status.setText("Segera dimulai! " + diff + " menit lagi");
            } else if (diff <= 120) {
                icon.setText("⛅");
                int jam2  = diff / 60;
                int menit = diff % 60;
                status.setText("Dalam " + (jam2 > 0 ? jam2 + " jam " : "") + menit + " menit lagi");
            } else {
                icon.setText("🌤️");
                status.setText("Masih " + (diff / 60) + " jam lagi, santai!");
            }
        } catch (Exception e) {
            icon.setText("📚");
            status.setText("Jadwal kuliah");
        }
    }
}