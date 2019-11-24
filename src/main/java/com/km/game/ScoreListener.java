package com.km.game;

import java.util.List;

public interface ScoreListener {
    void setScore(Score score);

    void setWarScore(int count, int black, int white);

    List<List<Integer>> getTrainProgress();

    void setTrainProgress(List<List<Integer>> list);
}
