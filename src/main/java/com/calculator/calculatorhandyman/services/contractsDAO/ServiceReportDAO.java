package com.calculator.calculatorhandyman.services.contractsDAO;

import com.calculator.calculatorhandyman.models.dto.ResponseDTO;
import com.calculator.calculatorhandyman.models.entities.ServiceReport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ServiceReportDAO {
    ServiceReport save(ServiceReport entity);
    ResponseDTO  getServiceReport(String technician, long week) ;
}
