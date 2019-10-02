package com.km.ui.board;

import com.km.game.Score;

public interface ScoreListener {
    void setScore(Score score);

    void setWarScore(int count, int black, int white);
}
