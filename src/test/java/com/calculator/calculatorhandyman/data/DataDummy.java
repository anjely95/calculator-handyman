package com.calculator.calculatorhandyman.data;

import com.calculator.calculatorhandyman.models.dto.ResponseDTO;
import com.calculator.calculatorhandyman.models.entities.ServiceReport;

import java.time.LocalDateTime;

public class DataDummy {
    public static ServiceReport report01() {
        ServiceReport serviceReport = new ServiceReport(1, "212", "333", LocalDateTime.of(2021,11,04,07,00), LocalDateTime.of(2021,11,04,11,00));
        return serviceReport;
    }

    public static ServiceReport report02() {
        ServiceReport serviceReport = new ServiceReport(2, "212", "333", LocalDateTime.of(2021,11,04,20,00), LocalDateTime.of(2021,11,05,02,00));
        return serviceReport;
    }

    public static ServiceReport report03() {
        ServiceReport serviceReport = new ServiceReport(3, "212", "333", LocalDateTime.of(2021,11,05,03,00), LocalDateTime.of(2021,11,05,20,00));
        return serviceReport;
    }

    public static ResponseDTO response01() {
        ResponseDTO responseDTO = new ResponseDTO(3L, 0L, 3L,3L, 0L, 3L);
        return responseDTO;
    }


}
