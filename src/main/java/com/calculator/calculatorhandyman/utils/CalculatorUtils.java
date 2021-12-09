package com.calculator.calculatorhandyman.utils;

import com.calculator.calculatorhandyman.models.dto.ResponseDTO;
import org.apache.coyote.Response;
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

   /* private void minutesToHours(ResponseDTO dto) {

    }*/

    public void minutesToHoursObj(ResponseDTO dto) {
        dto.setNormalDayTime(minutesToHours(dto.getNormalDayTime()));
        dto.setNormalNightTime(minutesToHours(dto.getNormalNightTime()));
        dto.setOvertimeDay(minutesToHours(dto.getOvertimeDay()));
        dto.setOvertimeNight(minutesToHours(dto.getOvertimeNight()));
        dto.setNormalSundayTime(minutesToHours(dto.getNormalSundayTime()));
        dto.setOvertimeSunday(minutesToHours(dto.getOvertimeSunday()));
    }

    public void setValuesDay( Long normalMinutes, Long overtimeMinutes ,ResponseDTO obj) {

        if ( normalMinutes != null && normalMinutes > 0L) {
            obj.setNormalDayTime(obj.getNormalDayTime() + normalMinutes);
        }

        if (overtimeMinutes != null && overtimeMinutes > 0L) {
            obj.setOvertimeDay(obj.getOvertimeDay() + overtimeMinutes);

        }
    }

    public void setValuesNight(Long normalMinutes, Long overtimeMinutes, ResponseDTO obj) {
        if ( normalMinutes != null && normalMinutes > 0L) {
            obj.setNormalNightTime(obj.getNormalNightTime() + normalMinutes);
        }

        if (overtimeMinutes != null && overtimeMinutes > 0L) {
            obj.setOvertimeNight(obj.getOvertimeNight() + overtimeMinutes);
        }

    }

    public void setValueSunday(Long normalMinutes, Long overtimeMinutes, ResponseDTO obj) {
        if ( normalMinutes != null && normalMinutes > 0L) {
            obj.setNormalSundayTime(obj.getNormalSundayTime() + normalMinutes);
        }

        if (overtimeMinutes != null && overtimeMinutes > 0L) {
            obj.setOvertimeSunday(obj.getOvertimeSunday()+ overtimeMinutes);
        }
    }
}
