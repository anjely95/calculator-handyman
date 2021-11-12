package com.calculator.calculatorhandyman.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "service_report")
public class ServiceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="report_id")
    private Integer reportId;
    @Column(nullable = false, name="technician_id")
    private String technicianId;
    @Column(nullable = false, name="service_id")
    private String serviceId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, name="start_date_time")
    private LocalDateTime startDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, name="end_date_time")
    private LocalDateTime endDateTime;
}
