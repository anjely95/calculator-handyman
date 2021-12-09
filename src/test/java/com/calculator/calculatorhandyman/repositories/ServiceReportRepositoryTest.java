package com.calculator.calculatorhandyman.repositories;

import com.calculator.calculatorhandyman.data.DataDummy;
import com.calculator.calculatorhandyman.models.entities.ServiceReport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ServiceReportRepositoryTest {

    @Autowired
    ServiceReportRepository serviceReportRepository;

    @BeforeEach
    void setUp() {
        //Given
        serviceReportRepository.save(DataDummy.report01());
        serviceReportRepository.save(DataDummy.report02());
        serviceReportRepository.save(DataDummy.report03());
    }

    @AfterEach
    void tearDown() {
        serviceReportRepository.deleteAll();
    }

    @Test
    void getServiceReportBetween() {
        //when
        List<ServiceReport> expected = (List<ServiceReport>) serviceReportRepository.getServiceReportBetween("212", LocalDateTime.of(2021,11,04,0,0), LocalDateTime.of(2021,11,05,23,59));
        System.out.println("HELLO: "+expected.size());
        //then
        assertThat((expected).size() == 3).isTrue();
    }


}