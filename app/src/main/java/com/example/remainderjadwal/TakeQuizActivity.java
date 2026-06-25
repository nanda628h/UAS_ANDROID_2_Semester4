package com.example.remainderjadwal;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TakeQuizActivity extends AppCompatActivity {

    private TextView tvTitle, tvQuestion, tvProgress, tvTimer;
    private RadioGroup rgOptions;
    private Button btnNext;
    private View rootView;

    private Quiz quiz;
    private int currentIndex = 0;
    private int score = 0;
    private int correctCount = 0;
    private int consecCorrect = 0;
    private final ArrayList<Integer> selectedAnswers = new ArrayList<>();

    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private ValueAnimator panicAnimator;
    private boolean isPanicMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        tvTitle    = findViewById(R.id.tvTitle);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        tvTimer    = findViewById(R.id.tvTimer);
        rgOptions  = findViewById(R.id.rgOptions);
        btnNext    = findViewById(R.id.btnNext);
        rootView   = findViewById(R.id.rootLayout);

        quiz = (Quiz) getIntent().getSerializableExtra("quiz");

        if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            Toast.makeText(this, "Quiz tidak valid!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(quiz.getTitle());

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            selectedAnswers.add(-1);
        }

        showQuestion();
        btnNext.setOnClickListener(v -> nextQuestion());

        if (quiz.getTimerMinutes() > 0) {
            tvTimer.setVisibility(View.VISIBLE);
            startTimer(quiz.getTimerMinutes() * 60 * 1000L);
        } else {
            tvTimer.setVisibility(View.GONE);
        }
    }

    private void startTimer(long millisTotal) {
        timerRunning = true;
        countDownTimer = new CountDownTimer(millisTotal, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                tvTimer.setText(String.format("⏱ %02d:%02d", minutes, seconds));

                if (millisUntilFinished <= 60000 && !isPanicMode) {
                    startPanicMode();
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("⏱ 00:00");
                stopPanicMode();
                Toast.makeText(TakeQuizActivity.this, "Waktu habis!", Toast.LENGTH_SHORT).show();
                calculateFinalScore();
            }
        }.start();
    }

    private void startPanicMode() {
        isPanicMode = true;
        tvTimer.setTextColor(Color.parseColor("#EF4444"));

        panicAnimator = ValueAnimator.ofArgb(
                Color.parseColor("#0F172A"),
                Color.parseColor("#2D0A0A")
        );
        panicAnimator.setDuration(800);
        panicAnimator.setRepeatCount(ValueAnimator.INFINITE);
        panicAnimator.setRepeatMode(ValueAnimator.REVERSE);
        panicAnimator.addUpdateListener(animator -> {
            if (rootView != null) {
                rootView.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        panicAnimator.start();

        ObjectAnimator shakeTimer = ObjectAnimator.ofFloat(tvTimer, "translationX", -8f, 8f);
        shakeTimer.setDuration(100);
        shakeTimer.setRepeatCount(ValueAnimator.INFINITE);
        shakeTimer.setRepeatMode(ValueAnimator.REVERSE);
        shakeTimer.start();
    }

    private void stopPanicMode() {
        isPanicMode = false;
        if (panicAnimator != null) panicAnimator.cancel();
        if (rootView != null) rootView.setBackgroundColor(Color.parseColor("#0F172A"));
        tvTimer.setTranslationX(0);
        tvTimer.setTextColor(Color.parseColor("#F8FAFC"));
    }

    private void showQuestion() {
        Question q = quiz.getQuestions().get(currentIndex);

        tvProgress.setText((currentIndex + 1) + " / " + quiz.getQuestions().size());
        tvQuestion.setText(q.getPertanyaan());

        rgOptions.removeAllViews();

        List<String> pilihan = q.getPilihan();
        for (int i = 0; i < pilihan.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(pilihan.get(i));
            rb.setId(i);
            rb.setTextSize(15);
            rb.setPadding(24, 20, 24, 20);
            rb.setTextColor(Color.parseColor("#F1F5F9"));

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 8, 0, 8);
            rb.setLayoutParams(params);

            rgOptions.addView(rb);
        }

        int prevSelected = selectedAnswers.get(currentIndex);
        if (prevSelected != -1) {
            RadioButton rb = rgOptions.findViewById(prevSelected);
            if (rb != null) rb.setChecked(true);
        }

        if (currentIndex == quiz.getQuestions().size() - 1) {
            btnNext.setText("Selesai");
        } else {
            btnNext.setText("Selanjutnya");
        }
    }

    private void nextQuestion() {
        int checkedId = rgOptions.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Pilih jawaban dulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedAnswers.set(currentIndex, checkedId);

        Question currentQuestion = quiz.getQuestions().get(currentIndex);
        int correctIndex = currentQuestion.getCorrectIndex();

        if (checkedId == correctIndex) {
            consecCorrect++;
            BadgeManager.checkConsecCorrect(this, consecCorrect);
        } else {
            consecCorrect = 0;

            List<String> pilihan = currentQuestion.getPilihan();
            String jawabanUserText = (checkedId >= 0 && checkedId < pilihan.size())
                    ? pilihan.get(checkedId) : "?";
            String jawabanBenarText = (correctIndex >= 0 && correctIndex < pilihan.size())
                    ? pilihan.get(correctIndex) : "?";

            WrongAnswer wa = new WrongAnswer(
                    quiz.getTitle(),
                    currentQuestion.getPertanyaan(),
                    jawabanUserText,
                    jawabanBenarText
            );
            MuseumStorage.addWrongAnswer(this, wa);
        }

        if (currentIndex < quiz.getQuestions().size() - 1) {
            currentIndex++;
            showQuestion();
        } else {
            calculateFinalScore();
        }
    }

    private void calculateFinalScore() {
        if (countDownTimer != null) countDownTimer.cancel();
        stopPanicMode();

        correctCount = 0;
        List<Question> questions = quiz.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            int selected = selectedAnswers.get(i);
            if (selected != -1 && selected == questions.get(i).getCorrectIndex()) {
                correctCount++;
            }
        }

        score = (int) ((correctCount / (double) questions.size()) * 100);

        boolean usedTimer = quiz.getTimerMinutes() > 0;
        BadgeManager.checkQuizCompleted(this, score, usedTimer);

        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("correct", correctCount);
        intent.putExtra("total", questions.size());
        intent.putExtra("quiz", quiz);
        intent.putIntegerArrayListExtra("selectedAnswers", selectedAnswers);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
        if (panicAnimator != null) panicAnimator.cancel();
    }
}