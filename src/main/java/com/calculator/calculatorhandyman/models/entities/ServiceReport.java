package com.calculator.calculatorhandyman.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


import java.time.LocalDateTime;

@Entity
@Table(name = "service_report")
public class ServiceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="report_id")
    @Getter @Setter
    private Integer reportId;
    @NotNull
    @NotEmpty(message = "Debe de ingresar el id del técnico")
    @Column(nullable = false, name="technician_id")
    @Getter @Setter
    private String technicianId;
    @Column(nullable = false, name="service_id")
    @NotNull
    @NotEmpty(message = "Debe de ingresar el id del servicio")
    @Getter @Setter
    private String serviceId;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, name="start_date_time")
    @NotNull(message = "La fecha de inicio es requerida")
    @Getter @Setter
    private LocalDateTime startDateTime;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(nullable = false, name="end_date_time")
    @NotNull(message = "La fecha de fin es requerida")
    @Getter @Setter
    private LocalDateTime endDateTime;

    public ServiceReport(Integer reportId, String technicianId, String serviceId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.reportId = reportId;
        this.technicianId = technicianId;
        this.serviceId = serviceId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public ServiceReport() {
    }
}
