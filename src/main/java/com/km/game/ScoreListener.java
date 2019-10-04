package com.km.game;

public interface ScoreListener {
    void setScore(Score score);

    void setWarScore(int count, int black, int white);
}
