package com.example.remainderjadwal;

import java.io.Serializable;

public class WrongAnswer implements Serializable {
    private String quizTitle;
    private String pertanyaan;
    private String jawabanUser;
    private String jawabanBenar;
    private String alasan; // user isi sendiri
    private long timestamp;

    public WrongAnswer(String quizTitle, String pertanyaan, String jawabanUser, String jawabanBenar) {
        this.quizTitle = quizTitle;
        this.pertanyaan = pertanyaan;
        this.jawabanUser = jawabanUser;
        this.jawabanBenar = jawabanBenar;
        this.alasan = "";
        this.timestamp = System.currentTimeMillis();
    }

    public String getQuizTitle() { return quizTitle; }
    public String getPertanyaan() { return pertanyaan; }
    public String getJawabanUser() { return jawabanUser; }
    public String getJawabanBenar() { return jawabanBenar; }
    public String getAlasan() { return alasan; }
    public long getTimestamp() { return timestamp; }
    public void setAlasan(String alasan) { this.alasan = alasan; }
}