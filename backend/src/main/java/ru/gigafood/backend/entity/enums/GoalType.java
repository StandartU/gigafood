package ru.gigafood.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum GoalType {
    LOSE, INCREASE_STR, KEEP_FIT
}
