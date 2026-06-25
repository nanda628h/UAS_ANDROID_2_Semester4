package com.example.remainderjadwal;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StatistikActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistik);

        TextView tvTotalMain = findViewById(R.id.tvTotalMain);
        TextView tvRataRata = findViewById(R.id.tvRataRata);
        TextView tvTertinggi = findViewById(R.id.tvTertinggi);
        TextView tvTerendah = findViewById(R.id.tvTerendah);
        LineChart lineChart = findViewById(R.id.lineChart);

        // Tombol kembali di paling atas, selalu bisa diklik
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ArrayList<ScoreHistory> history = ScoreHistoryStorage.getHistory(this);

        if (history.isEmpty()) {
            tvTotalMain.setText("Total Main: 0");
            tvRataRata.setText("Rata-rata: -");
            tvTertinggi.setText("Tertinggi: -");
            tvTerendah.setText("Terendah: -");
            return;
        }

        // Hitung statistik
        int total = history.size();
        int tertinggi = 0;
        int terendah = 100;
        int jumlah = 0;

        for (ScoreHistory h : history) {
            jumlah += h.getScore();
            if (h.getScore() > tertinggi) tertinggi = h.getScore();
            if (h.getScore() < terendah) terendah = h.getScore();
        }

        int rataRata = jumlah / total;

        tvTotalMain.setText("Total Main: " + total + " kuis");
        tvRataRata.setText("Rata-rata: " + rataRata + "%");
        tvTertinggi.setText("🏆 Tertinggi: " + tertinggi + "%");
        tvTerendah.setText("📉 Terendah: " + terendah + "%");

        // Setup grafik
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (int i = 0; i < history.size(); i++) {
            entries.add(new Entry(i, history.get(i).getScore()));
            labels.add(sdf.format(new Date(history.get(i).getTimestamp())));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Skor Quiz");
        dataSet.setColor(ThemeManager.getAccentColor(this));
        dataSet.setCircleColor(ThemeManager.getAccentColor(this));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ThemeManager.getAccentColor(this));
        dataSet.setFillAlpha(50);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.setBackgroundColor(Color.parseColor("#1E293B"));
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMaximum(100f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.getXAxis().setGranularity(1f);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
}