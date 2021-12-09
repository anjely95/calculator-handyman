package com.calculator.calculatorhandyman.repositories;

import com.calculator.calculatorhandyman.models.entities.ServiceReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;


public interface ServiceReportRepository extends CrudRepository<ServiceReport, Integer> {
   @Query("SELECT s FROM ServiceReport s WHERE s.technicianId = ?1 AND s.startDateTime between  ?2 and ?3  order by s.startDateTime asc ")
   Iterable<ServiceReport> getServiceReportBetween(String technician, LocalDateTime startDate, LocalDateTime endDate);

}
