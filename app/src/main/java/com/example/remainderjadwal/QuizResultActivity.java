package com.example.remainderjadwal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class QuizResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        int score        = getIntent().getIntExtra("score", 0);
        int correctCount = getIntent().getIntExtra("correct", 0);
        int totalSoal    = getIntent().getIntExtra("total", 0);
        Quiz quiz        = (Quiz) getIntent().getSerializableExtra("quiz");
        ArrayList<Integer> selectedAnswers = getIntent().getIntegerArrayListExtra("selectedAnswers");

        // Simpan jawaban salah ke Museum
        if (quiz != null && selectedAnswers != null) {
            List<Question> questions = quiz.getQuestions();
            ArrayList<WrongAnswer> wrongAnswers = MuseumStorage.getWrongAnswers(this);

            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                int selected = selectedAnswers.get(i);
                if (selected == -1) continue;
                if (selected != q.getCorrectIndex()) {
                    List<String> pilihan = q.getPilihan();
                    String jawabanUser  = (selected < pilihan.size()) ? pilihan.get(selected) : "?";
                    int correctIdx      = q.getCorrectIndex();
                    String jawabanBenar = (correctIdx >= 0 && correctIdx < pilihan.size())
                            ? pilihan.get(correctIdx) : q.getJawabanBenar();

                    wrongAnswers.add(new WrongAnswer(
                            quiz.getTitle(),
                            q.getPertanyaan(),
                            jawabanUser,
                            jawabanBenar
                    ));
                }
            }
            MuseumStorage.saveWrongAnswers(this, wrongAnswers);
        }

        // Simpan histori skor untuk statistik
        if (quiz != null) {
            ScoreHistoryStorage.addHistory(this, new ScoreHistory(quiz.getTitle(), score));
        }

        // Simpan statistik profil
        SharedPreferences prefs = getSharedPreferences("profile_pref", MODE_PRIVATE);
        int totalSkor  = prefs.getInt("total_skor", 0) + score;
        int jumlahMain = prefs.getInt("jumlah_main", 0) + 1;
        prefs.edit().putInt("total_skor", totalSkor).putInt("jumlah_main", jumlahMain).apply();

        // Inisialisasi view
        ProgressBar progressBar  = findViewById(R.id.scoreProgressBar);
        TextView tvScoreText     = findViewById(R.id.tvScoreText);
        TextView tvScoreDetail   = findViewById(R.id.tvScoreDetail);
        TextView tvGrade         = findViewById(R.id.tvGrade);
        TextView tvQuizTitle     = findViewById(R.id.tvQuizTitle);
        Button btnBack           = findViewById(R.id.btnBack);
        Button btnShare          = findViewById(R.id.btnShare);
        CardView cardResult      = findViewById(R.id.cardResult);

        // Set data
        if (tvQuizTitle != null && quiz != null) tvQuizTitle.setText(quiz.getTitle());
        if (progressBar != null) progressBar.setProgress(score);
        if (tvScoreText != null) tvScoreText.setText(score + "%");
        if (tvScoreDetail != null)
            tvScoreDetail.setText("Benar " + correctCount + " dari " + totalSoal + " soal");

        // Grade berdasarkan skor
        if (tvGrade != null) {
            String grade;
            int gradeColor;
            if (score == 100) {
                grade = "⭐ Sempurna";
                gradeColor = Color.parseColor("#D97706");
            } else if (score >= 80) {
                grade = "Sangat Baik";
                gradeColor = Color.parseColor("#22C55E");
            } else if (score >= 60) {
                grade = "Cukup Baik";
                gradeColor = Color.parseColor("#3B82F6");
            } else if (score >= 40) {
                grade = "Perlu Belajar";
                gradeColor = Color.parseColor("#F59E0B");
            } else {
                grade = "Ayo Semangat!";
                gradeColor = Color.parseColor("#EF4444");
            }
            tvGrade.setText(grade);
            tvGrade.setTextColor(gradeColor);
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                if (cardResult == null) return;
                cardResult.setDrawingCacheEnabled(true);
                cardResult.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(
                        cardResult.getWidth(),
                        cardResult.getHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                cardResult.draw(canvas);
                cardResult.setDrawingCacheEnabled(false);

                try {
                    File cachePath = new File(getCacheDir(), "images");
                    cachePath.mkdirs();
                    File file = new File(cachePath, "hasil_quiz.png");
                    FileOutputStream stream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    Uri imageUri = FileProvider.getUriForFile(
                            this,
                            getPackageName() + ".fileprovider",
                            file
                    );

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/png");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Bagikan via"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}