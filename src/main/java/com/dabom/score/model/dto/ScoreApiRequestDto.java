package com.dabom.score.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreApiRequestDto {
    private double score;
    private Integer videoIdx;
}
