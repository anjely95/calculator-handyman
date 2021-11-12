package com.calculator.calculatorhandyman.models.dto;

import com.calculator.calculatorhandyman.utils.CalculatorUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

    private Long normalDayTime = 0L; //hoursDayN;
    private Long normalNightTime = 0L; //hoursNightN
    private Long overtimeDay = 0L; //hoursDayE
    private Long overtimeNight = 0L; //hoursNightE
    private Long normalSundayTime = 0L; //hoursSundayN
    private Long overtimeSunday = 0L; //hoursSundayE


}
