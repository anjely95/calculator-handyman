package com.calculator.calculatorhandyman.utils;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;

@Service
public class CalculatorUtils {

    public CalculatorUtils() {

    }
    public LocalDate getDayOfWeek(Long calendarWeek, DayOfWeek dayOfWeek) {
        return  LocalDate.now()
                .with(WeekFields.ISO.weekOfWeekBasedYear(), calendarWeek) // week of year
                .with(WeekFields.ISO.dayOfWeek(), dayOfWeek.getValue()); // day of week
    }


    public Long minutesToHours (Long minutes) {
        Long rMinutes = 0L;
        Long hours = 0L;
        if (minutes > 0L) {
            rMinutes = minutes%60;
            hours = minutes/60;
        }

        return hours;
    }

    public Long getDuration (LocalDateTime startTime, LocalDateTime endTime) {
        Duration between = Duration.between(startTime, endTime);
        return between.toMinutes();
    }
}
