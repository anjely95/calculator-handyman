package com.calculator.calculatorhandyman.controllers;

import com.calculator.calculatorhandyman.models.dto.ResponseDTO;
import com.calculator.calculatorhandyman.models.entities.ServiceReport;
import com.calculator.calculatorhandyman.services.contractsDAO.ServiceReportDAO;
import com.calculator.calculatorhandyman.services.implementationsDAO.ServiceReportDAOImpl;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ServiceReportController {

    private final ServiceReportDAO serviceReportDAO;

    @Autowired
    public ServiceReportController(ServiceReportDAO serviceReportDAO) {
        this.serviceReportDAO = serviceReportDAO;
    }

    @PostMapping
    public ResponseEntity<?> saveReport(@Valid @RequestBody ServiceReport serviceReport, BindingResult result) {
       Map<String, Object> message = new HashMap<>();
        if (result.hasErrors()) {
            message.put("success", Boolean.FALSE);
            message.put("errors", getError(result));
            return ResponseEntity.badRequest().body(message);
        }

        ServiceReport save = serviceReportDAO.save(serviceReport);
        System.out.println("ingresa real11");
        message.put("success", Boolean.TRUE);
        message.put("data", save);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/{technicianId}/{week}")
    public ResponseEntity<?> getHours(@PathVariable String technicianId, @PathVariable long week) {
        ResponseDTO responseDTO = serviceReportDAO.getServiceReport(technicianId, week);
        Map<String, Object> message = new HashMap<>();
        message.put("success", Boolean.TRUE);
        message.put("data", responseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

 //.with(WeekFields.ISO.weekBasedYear(), 2021) // year

    protected Map<String, Object> getError(BindingResult result){
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

}
