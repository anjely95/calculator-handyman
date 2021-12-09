package com.calculator.calculatorhandyman.services.implementationsDAO;

import com.calculator.calculatorhandyman.models.dto.ResponseDTO;
import com.calculator.calculatorhandyman.models.entities.ServiceReport;
import com.calculator.calculatorhandyman.repositories.ServiceReportRepository;
import com.calculator.calculatorhandyman.services.contractsDAO.ServiceReportDAO;
import com.calculator.calculatorhandyman.utils.CalculatorUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
public class ServiceReportDAOImpl implements ServiceReportDAO {

    protected final ServiceReportRepository  repository;
    protected  final LocalTime dayTimeStart = LocalTime.parse("07:00");
    protected  final LocalTime dayTimeEnd = LocalTime.parse("20:00");

    protected  final LocalTime nightTimeStart = LocalTime.parse("20:00");
    protected  final LocalTime nightTimeEnd = LocalTime.parse("07:00");
    protected final Long WEEKLY_MINUTES  = 2880L;

    protected  final int diurnal = 1;
    protected  final int nocturnal = 2;
    protected  final int sunday = 3;

    boolean OVERTIME;
    Long  totalWeeklyMinutes ;
    ResponseDTO responseDTO;


    private CalculatorUtils utils = new CalculatorUtils();

    public ServiceReportDAOImpl(ServiceReportRepository repository) {
        this.repository = repository;
    }


    @Override
    @Transactional
    public ServiceReport save(ServiceReport entity) {
        System.out.println("INGRESA SAVE REAL");
        return repository.save(entity);
    }


    @Override
    public ResponseDTO getServiceReport(String technician, long week) {
        System.out.println("SI INGRESA !!!");

       OVERTIME = false;
       totalWeeklyMinutes = 0L;
       responseDTO = new ResponseDTO();


        LocalDateTime startDate = utils.getDayOfWeek(week, DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endDateSaturday = utils.getDayOfWeek(week, DayOfWeek.SATURDAY).atTime(23,59);
        LocalDateTime dateStartSunday = utils.getDayOfWeek(week, DayOfWeek.SUNDAY).atStartOfDay();
        LocalDateTime dateEndSunday = utils.getDayOfWeek(week, DayOfWeek.SUNDAY).atTime(23,59);

        List<ServiceReport> list = (List<ServiceReport>) repository.getServiceReportBetween(technician,startDate,endDateSaturday);
        List<ServiceReport> listSunday = (List<ServiceReport>) repository.getServiceReportBetween(technician, dateStartSunday, dateEndSunday);

        if (!list.isEmpty()) {

                list.forEach(obj -> {
                    int startDateTime = obj.getStartDateTime().getHour();
                    if (startDateTime >= dayTimeStart.getHour() && startDateTime < dayTimeEnd.getHour())  {
                        calculateWorkHours(obj, diurnal,  dateEndSunday);
                    } else if (startDateTime >= nightTimeStart.getHour() ||  startDateTime < nightTimeEnd.getHour()) {
                        calculateWorkHours(obj, nocturnal,  dateEndSunday);

                    }
                });
        }

        if (!listSunday.isEmpty()) {
            listSunday.forEach(objS -> {
                   sundayDay(objS, dateEndSunday);
                });
        }

        utils.minutesToHoursObj(responseDTO);
        return responseDTO;
    }


    private void sundayDay(ServiceReport obj, LocalDateTime dateEndSunday) {
        obj = validateAfterWeek(obj, dateEndSunday);
        if (OVERTIME) {
            utils.setValueSunday(0L, utils.getDuration(obj.getStartDateTime(), obj.getEndDateTime()), responseDTO);
        }else {
            validateHoursOfOvertime(sunday, utils.getDuration(obj.getStartDateTime(), obj.getEndDateTime()));
        }

    }

    private ServiceReport validateAfterWeek(ServiceReport obj, LocalDateTime dateEndSunday) {
        if (obj.getEndDateTime().isAfter(dateEndSunday)) {
            obj.setEndDateTime(obj.getEndDateTime().with(dateEndSunday));
        }
        return obj;
    }


    private void calculateWorkHours( ServiceReport obj, Integer typeWorkDay,  LocalDateTime dateEndSunday) {

        boolean setHoursForSunday = false;
        Long mainShift  = 0L;
        Long nextShift = 0L;

        LocalDateTime dayTimeEnd = validateDayTimeEnd(obj, typeWorkDay);

        if (dayTimeEnd != null) {
            if (obj.getEndDateTime().isAfter(dayTimeEnd)) {
                mainShift = utils.getDuration(obj.getStartDateTime(), dayTimeEnd);
                nextShift = utils.getDuration(dayTimeEnd, obj.getEndDateTime());
            } else {
                mainShift = utils.getDuration(obj.getStartDateTime(), obj.getEndDateTime());
            }
        }


      // if (obj.getStartDateTime().getDayOfWeek() == endDateSaturday.getDayOfWeek() && obj.getEndDateTime().getDayOfWeek() == dateEndSunday.getDayOfWeek()) {
       if (obj.getEndDateTime().getDayOfWeek() == dateEndSunday.getDayOfWeek()) {

           Long timeForSunday = 24L - obj.getStartDateTime().getHour(); //nightTimeStart.getHour();
           timeForSunday = timeForSunday * 60L;

            Long totalWorkTime = mainShift + nextShift;

            if (totalWorkTime > timeForSunday) {
                nextShift = totalWorkTime - timeForSunday;
                mainShift = timeForSunday;
                setHoursForSunday = true;
            }
        }

        setWorkHours(typeWorkDay,setHoursForSunday, mainShift, nextShift );

    }


    // SABER EL FINAL DE LA JORNADA
    private LocalDateTime validateDayTimeEnd(ServiceReport obj, Integer typeWorkDay) {
        LocalDateTime dayTimeEnd;
        if (typeWorkDay == diurnal) {
            dayTimeEnd = (obj.getStartDateTime()).with(this.dayTimeEnd);
        }else  {
            dayTimeEnd = ((obj.getStartDateTime()).with(this.nightTimeEnd));
            if (obj.getStartDateTime().getHour() >= 20 && obj.getStartDateTime().getHour() <= 23  ) {
                dayTimeEnd =  dayTimeEnd.plusDays(1);
            }
        }

        return dayTimeEnd;
    }


    private void setWorkHours (Integer typeWorkDay, Boolean setHoursForSunday, Long mainShift, Long nextShift ) {
        if (!OVERTIME) {
           validateHoursOfOvertime(typeWorkDay, mainShift);
            if (nextShift > 0) {
                if (setHoursForSunday) {
                    validateHoursOfOvertime(sunday, nextShift);
                }else {
                    validateHoursOfOvertime(typeWorkDay == diurnal? nocturnal:diurnal, nextShift);
                }
            }
        } else {
            if (typeWorkDay == diurnal ) validOvertimeDiurnal( setHoursForSunday, mainShift,  nextShift);
            if (typeWorkDay == nocturnal ) validOvertimeNocturnal( setHoursForSunday, mainShift, nextShift);
            update(mainShift + nextShift);
        }
    }


    private void validOvertimeDiurnal( Boolean setHoursForSunday, Long mainShift, Long nextShift) {
        utils.setValuesDay(0L,mainShift, responseDTO);
        if (setHoursForSunday) {
            utils.setValueSunday(0L ,nextShift, responseDTO);
        } else {
            utils.setValuesNight(0L,nextShift, responseDTO);
        }
    }

    private void validOvertimeNocturnal( Boolean setHoursForSunday, Long mainShift, Long nextShift) {
        utils.setValuesNight(0L,mainShift, responseDTO);
        if (setHoursForSunday) {
            utils.setValueSunday(0L ,nextShift, responseDTO);
        } else {
            utils.setValuesDay( 0L,nextShift, responseDTO);
        }
    }

    private void validateHoursOfOvertime (Integer typeWorkDay, Long minutes) {
        Long restMinutesNormal = 0L;
        Long restMinutesAdd = 0L;
        if (!OVERTIME) {
            Long res = WEEKLY_MINUTES  - totalWeeklyMinutes;
            if (minutes > res) {
                restMinutesNormal = res;
                restMinutesAdd = minutes - res;
            } else {
                restMinutesNormal = minutes;
            }
        } else {
             restMinutesAdd = minutes;
        }


        switch (typeWorkDay) {
            case diurnal: utils.setValuesDay(restMinutesNormal, restMinutesAdd, responseDTO); break;
            case nocturnal:  utils.setValuesNight(restMinutesNormal, restMinutesAdd, responseDTO); break;
            case sunday:  utils.setValueSunday(restMinutesNormal, restMinutesAdd, responseDTO); break;
            default: break;
        }

        update(restMinutesNormal + restMinutesAdd );
    }

    private void update(Long total) {
        totalWeeklyMinutes += total;
        OVERTIME = totalWeeklyMinutes >= WEEKLY_MINUTES;
    }

}
