package com.dabom.common.utils;

import com.dabom.score.model.entity.Score;

import java.util.List;

public class ScoreAvg {

    private Double scoreAvg(List<Score> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (Score score : scores) {
            sum += score.getScore();
        }
        return sum / scores.size();
    }
}
