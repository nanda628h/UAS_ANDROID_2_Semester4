package com.example.remainderjadwal;

import java.io.Serializable;

public class ScoreHistory implements Serializable {
    private String quizTitle;
    private int score;
    private long timestamp;

    public ScoreHistory(String quizTitle, int score) {
        this.quizTitle = quizTitle;
        this.score = score;
        this.timestamp = System.currentTimeMillis();
    }

    public String getQuizTitle() { return quizTitle; }
    public int getScore() { return score; }
    public long getTimestamp() { return timestamp; }
}