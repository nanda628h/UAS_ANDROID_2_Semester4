package com.example.remainderjadwal;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateQuizActivity extends AppCompatActivity {

    EditText etTitle, etTimer, etQ, et0, et1, et2, et3, etCorrect;
    Button btnAddQ, btnSave;
    Quiz current;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_create_quiz);

        etTitle = findViewById(R.id.etTitle);
        etTimer = findViewById(R.id.etTimer);
        etQ = findViewById(R.id.etQuestion);
        et0 = findViewById(R.id.etOpt0);
        et1 = findViewById(R.id.etOpt1);
        et2 = findViewById(R.id.etOpt2);
        et3 = findViewById(R.id.etOpt3);
        etCorrect = findViewById(R.id.etCorrect);
        btnAddQ = findViewById(R.id.btnAddQuestion);
        btnSave = findViewById(R.id.btnSaveQuiz);

        current = new Quiz("Untitled");

        btnAddQ.setOnClickListener(v -> {
            String q = etQ.getText().toString().trim();
            String[] opts = new String[]{
                    et0.getText().toString().trim(),
                    et1.getText().toString().trim(),
                    et2.getText().toString().trim(),
                    et3.getText().toString().trim()
            };

            int idx;
            try {
                idx = Integer.parseInt(etCorrect.getText().toString().trim());
            } catch (Exception e) {
                idx = -1;
            }

            if (q.isEmpty()) {
                Toast.makeText(this, "Isi kolom pertanyaan", Toast.LENGTH_SHORT).show();
                return;
            }
            if (opts[0].isEmpty() || opts[1].isEmpty() || opts[2].isEmpty() || opts[3].isEmpty()) {
                Toast.makeText(this, "Semua pilihan jawaban harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idx < 0 || idx > 3) {
                Toast.makeText(this, "Index jawaban harus antara 0 dan 3", Toast.LENGTH_SHORT).show();
                return;
            }

            current.addQuestion(new Question(q, Arrays.asList(opts), String.valueOf(idx)));

            etQ.setText("");
            et0.setText("");
            et1.setText("");
            et2.setText("");
            et3.setText("");
            etCorrect.setText("");

            Toast.makeText(this, "Pertanyaan ditambahkan (" + current.getQuestions().size() + ")", Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Judul kuis tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            if (current.getQuestions().isEmpty()) {
                Toast.makeText(this, "Tambahkan minimal 1 pertanyaan", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ambil timer
            int timer = 0;
            String timerStr = etTimer.getText().toString().trim();
            if (!timerStr.isEmpty()) {
                try {
                    timer = Integer.parseInt(timerStr);
                    if (timer < 0) timer = 0;
                } catch (Exception e) {
                    timer = 0;
                }
            }

            current.setTitle(title);
            current.setTimerMinutes(timer);

            ArrayList<Quiz> list = QuizStorage.getQuizzes(this);
            if (list == null) list = new ArrayList<>();
            list.add(current);
            QuizStorage.saveQuizzes(this, list);
            BadgeManager.checkQuizCreated(this, false);
            BadgeManager.checkQuizCount(this, list.size());

            String timerInfo = timer > 0 ? " (Timer: " + timer + " menit)" : "";
            Toast.makeText(this, "Kuis '" + title + "' berhasil disimpan" + timerInfo, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}