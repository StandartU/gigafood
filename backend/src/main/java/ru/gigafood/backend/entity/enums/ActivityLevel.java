package ru.gigafood.backend.entity.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ActivityLevel {
    SPORT, NORMAL, LAZY
}
    