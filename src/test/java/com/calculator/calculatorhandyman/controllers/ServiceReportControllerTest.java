package com.calculator.calculatorhandyman.controllers;

import com.calculator.calculatorhandyman.models.entities.ServiceReport;
import com.calculator.calculatorhandyman.services.contractsDAO.ServiceReportDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.print.attribute.standard.Media;
//import java.time.LocalDateTime;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static com.calculator.calculatorhandyman.data.DataDummy.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(ServiceReportController.class)
class ServiceReportControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServiceReportDAO serviceReportDAO;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }


    @Test
    void saveReport() throws Exception {
        //Given
        ServiceReport serviceReport = new ServiceReport(null, "212", "333", LocalDateTime.of(2021,11,04,07,00), LocalDateTime.of(2021,11,04,11,00));
        when(serviceReportDAO.save(any())).then(invocation -> {
            ServiceReport s = invocation.getArgument(0);
            s.setReportId(3);
            return s;
        });
        ObjectMapper objectMapper =
                new ObjectMapper().registerModule(new JavaTimeModule())
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //When
        mvc.perform(post("/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(serviceReport)))
        //Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reportId").value(3));

        verify(serviceReportDAO).save(any());
    }

    @Test
    void saveReportError() throws Exception {
        //Given
        ServiceReport serviceReport = new ServiceReport(null, "212", "333", null, LocalDateTime.of(2021,11,04,11,00));
        when(serviceReportDAO.save(serviceReport)).then(invocation -> {
            ServiceReport s = invocation.getArgument(0);
            s.setReportId(3);
            return s;
        });
        ObjectMapper objectMapper =
                new ObjectMapper().registerModule(new JavaTimeModule())
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //When
        mvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(serviceReport)))
                //Then
                .andExpect(status().is(400))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.startDateTime").value("La fecha de inicio es requerida"));
    }

    @Test
    void getHours() throws Exception {
        //Given
        when(serviceReportDAO.getServiceReport(anyString(),anyLong())).thenReturn(response01());

        //when

        mvc.perform(get("/report/123/44").contentType(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.normalDayTime").value(3L));

        verify(serviceReportDAO).getServiceReport(anyString(),anyLong());
    }
}