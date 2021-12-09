package com.calculator.calculatorhandyman.services.implementationsDAO;

import com.calculator.calculatorhandyman.models.dto.ResponseDTO;
import com.calculator.calculatorhandyman.repositories.ServiceReportRepository;
import com.calculator.calculatorhandyman.services.contractsDAO.ServiceReportDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static com.calculator.calculatorhandyman.data.DataDummy.*;

@RunWith(MockitoJUnitRunner.class)
class ServiceReportDAOImplTest {


    ServiceReportDAO serviceReportDAO;
    @Mock
    ServiceReportRepository serviceReportRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serviceReportDAO = new ServiceReportDAOImpl(serviceReportRepository);
    }

    @Test
    void getServiceReport() {
        // Given

        when(serviceReportRepository.getServiceReportBetween("212", LocalDateTime
                   .of(2021,11,01,0,0), LocalDateTime.of(2021,11,06,23,59)))
                .thenReturn(Arrays.asList(report01(),report02(),report03()));

        ResponseDTO expected =  new ResponseDTO();
        expected.setNormalDayTime(17L);
        expected.setNormalNightTime(10L);
        //when
        ResponseDTO obj = serviceReportDAO.getServiceReport("212",44L);

        System.out.println(obj);
        //Then
        assertThat(obj).isEqualTo(expected);

     //   verify(serviceReportRepository).getServiceReportBetween("212", LocalDateTime.of(2021,11,04,0,0), LocalDateTime.of(2021,11,05,23,59));

    }
}