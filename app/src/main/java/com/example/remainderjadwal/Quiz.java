package com.example.remainderjadwal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Quiz implements Serializable {
    private String id;
    private String title;
    private ArrayList<Question> questions;
    private int timerMinutes; // 0 = tidak ada timer

    public Quiz() {
        this.id = UUID.randomUUID().toString();
        this.questions = new ArrayList<>();
        this.timerMinutes = 0;
    }

    public Quiz(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.questions = new ArrayList<>();
        this.timerMinutes = 0;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public ArrayList<Question> getQuestions() { return questions; }
    public void setQuestions(ArrayList<Question> questions) { this.questions = questions; }
    public void addQuestion(Question q) { questions.add(q); }
    public int getTimerMinutes() { return timerMinutes; }
    public void setTimerMinutes(int timerMinutes) { this.timerMinutes = timerMinutes; }
}