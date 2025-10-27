package ru.gigafood.backend.tool;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateTools {
    public Date getStartOfWeek() {
    LocalDate now = LocalDate.now();
    LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
    return Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
}

    public Date getEndOfWeek() {
        LocalDate now = LocalDate.now();
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        return Date.from(endOfWeek.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
}
}
