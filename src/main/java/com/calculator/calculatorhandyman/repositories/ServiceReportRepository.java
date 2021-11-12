package com.calculator.calculatorhandyman.repositories;

import com.calculator.calculatorhandyman.models.entities.ServiceReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;


public interface ServiceReportRepository extends CrudRepository<ServiceReport, Integer> {
   @Query("SELECT s FROM ServiceReport s WHERE s.technicianId = ?1 AND (s.startDateTime between  ?2 and ?3) OR (s.endDateTime between  ?2 and ?3)  order by s.startDateTime asc ")
   Iterable<ServiceReport> getServiceReportBetween(String technician, LocalDateTime startDate, LocalDateTime endDate);

 /*  SELECT start_date_time, end_date_time, technician_id FROM calculator.service_report as s where s.technician_id = "888"
   and ((s.start_date_time BETWEEN "2021-11-15 00:00" AND "2021-11-21 23:59") or
(s.end_date_time BETWEEN "2021-11-15 00:00" AND "2021-11-21 23:59"));*/

   @Query("SELECT s FROM ServiceReport s WHERE s.technicianId = ?1 AND s.startDateTime = ?2")
   Iterable<ServiceReport> getServiceReportToDate(String technician, LocalDateTime Date);
}
