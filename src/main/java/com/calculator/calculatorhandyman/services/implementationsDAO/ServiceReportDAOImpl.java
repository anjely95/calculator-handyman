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
        return repository.save(entity);
    }

    @Override
    public ResponseDTO getServiceReport(String technician, long week) {
       OVERTIME = false;
       totalWeeklyMinutes = 0L;
       responseDTO = new ResponseDTO();


        LocalDateTime startDate = utils.getDayOfWeek(week, DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endDate = utils.getDayOfWeek(week, DayOfWeek.SATURDAY).atTime(23,59);
        LocalDateTime dateStartSunday = utils.getDayOfWeek(week, DayOfWeek.SUNDAY).atStartOfDay();
        LocalDateTime dateEndSunday = utils.getDayOfWeek(week, DayOfWeek.SUNDAY).atTime(23,59);

        List<ServiceReport> list = (List<ServiceReport>) repository.getServiceReportBetween(technician,startDate,endDate);
        List<ServiceReport> listSunday = (List<ServiceReport>) repository.getServiceReportBetween(technician, dateStartSunday, dateEndSunday);

        //List<ServiceReport> listSunday = (List<ServiceReport>) repository.getServiceReportToDate(technician, dateStartSunday);
        //System.out.println("listSunday DOMINGO: "+listSunday+ "technician " +technician);
        if (!list.isEmpty()) {
                list.forEach(obj -> {
                    System.out.println(" ---------------------------- "+obj);
                    int startDateTime = obj.getStartDateTime().getHour();
                    if (startDateTime >= dayTimeStart.getHour() && startDateTime < dayTimeEnd.getHour())  {

                        dayShift(obj, true, endDate, dateEndSunday);
                        System.out.println("diurna: "+ responseDTO);

                    } else if (startDateTime >= nightTimeStart.getHour() ||  startDateTime < nightTimeEnd.getHour()) {

                        dayShift(obj, false, endDate, dateEndSunday);
                        System.out.println("nocturna: "+ responseDTO);

                    }
                });
        }

        if (!listSunday.isEmpty()) {
            listSunday.forEach(objS -> {
                    //System.out.println("OBJS" + objS);
                   sundayDay(objS, dateEndSunday);
                });
        }

        System.out.println("ANTES DE CONVERT  :"+responseDTO);
        refactorData(responseDTO);
        System.out.println("DESPUES DE CONVERT  :"+responseDTO);

        return responseDTO;
    }


    private void sundayDay(ServiceReport obj, LocalDateTime dateEndSunday) {
        obj = validateAfterWeek(obj, dateEndSunday);
        if (OVERTIME) {
            setValueSunday(0L, utils.getDuration(obj.getStartDateTime(), obj.getEndDateTime()));
        }else {
            validateTimeE(3, utils.getDuration(obj.getStartDateTime(), obj.getEndDateTime()));
        }

    }

    // nuevo
    private ServiceReport validateAfterWeek(ServiceReport obj, LocalDateTime dateEndSunday) {
        if (obj.getEndDateTime().isAfter(dateEndSunday)) {
            obj.setEndDateTime(obj.getEndDateTime().with(dateEndSunday));
        }
        return obj;
    }

    private ResponseDTO dayShift( ServiceReport obj, Boolean workingDay, LocalDateTime saturday, LocalDateTime dateEndSunday) {

        obj = validateAfterWeek(obj,dateEndSunday); // nuevo


        boolean sundayDay = false;
        Long minP  = 0L;
        Long minS = 0L;
        LocalDateTime dayTimeEnd = null;
        if (workingDay) {
             dayTimeEnd = (obj.getStartDateTime()).with(this.dayTimeEnd);
        }else  {
            dayTimeEnd = ((obj.getStartDateTime()).with(this.nightTimeEnd));
            //if (obj.getStartDateTime().getDayOfWeek() == obj.getEndDateTime().getDayOfWeek()  ) {
            if (obj.getStartDateTime().getHour() >= 20 && obj.getStartDateTime().getHour() <= 23  ) {
                dayTimeEnd =  dayTimeEnd.plusDays(1);
            }
        }

        System.out.println("dayTimeEnd: "+dayTimeEnd);

        if (dayTimeEnd != null) {
            if (obj.getEndDateTime().isAfter(dayTimeEnd)) {
                minP = utils.getDuration(obj.getStartDateTime(), dayTimeEnd);
                minS = utils.getDuration(dayTimeEnd, obj.getEndDateTime());
            } else {
                minP = utils.getDuration(obj.getStartDateTime(), obj.getEndDateTime());
            }
        }

        System.out.println("RESULTADOS MINUTOS minP: "+minP + "  mins "+minS);

       // if (obj.getStartDateTime().getDayOfWeek() == saturday.getDayOfWeek() && obj.getEndDateTime().getDayOfWeek() == dateEndSunday.getDayOfWeek()) {
        if (obj.getEndDateTime().getDayOfWeek() == dateEndSunday.getDayOfWeek()) {

            Long range = 24L - obj.getStartDateTime().getHour(); //nightTimeStart.getHour();
            range = range * 60L;

            Long range2 = minP + minS;

            if (range2 > range) {
                Long rest = range2 - range;
                minP = range;
                minS = rest;

                sundayDay = true;
            }
            System.out.println("VALIDACON SABADO----- "+minS + "   -- "+minP+ " raange "+range+ " range 2 "+ range2);
            //  setValueSunday(0L, minS);

        }

        if (!OVERTIME) {

            validateTimeE(workingDay? 1: 2, minP);
            if (minS > 0) {
                if (sundayDay) {
                    validateTimeE(3, minS);
                }else {
                    validateTimeE(workingDay? 2:1, minS);
                }
            }
        } else {
            if (workingDay) {
                setValuesDay(responseDTO, 0L,minP);

                if (sundayDay) {
                    setValueSunday(0L ,minS);
                } else {
                    setValuesNight(0L,minS);
                }
            } else {
                if (sundayDay) {
                    setValueSunday(0L ,minS);
                } else {
                    setValuesDay(responseDTO, 0L,minS);
                }
                setValuesNight(0L,minP);
            }
            update(minP + minS);
        }

        return responseDTO;
    }


    private ResponseDTO validateTimeE (Integer day, Long minutes) {
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

        switch (day) {
            case 1 : setValuesDay(responseDTO,restMinutesNormal, restMinutesAdd); break;
            case 2:  setValuesNight(restMinutesNormal, restMinutesAdd); break;
            case 3:  setValueSunday(restMinutesNormal, restMinutesAdd); break;
            default: break;
        }

        update(restMinutesNormal + restMinutesAdd );
        return responseDTO;
    }

    private Long update(Long total) {
        totalWeeklyMinutes += total;
        updateE();
        return WEEKLY_MINUTES;
    }

    private boolean updateE() {
        OVERTIME = totalWeeklyMinutes >= WEEKLY_MINUTES;
        System.out.println("OVERTIME !!!!"+OVERTIME +" -totalWeeklyMinutes: "+totalWeeklyMinutes +" -WEEKLY_MINUTES: "+ WEEKLY_MINUTES);
        return OVERTIME;
    }


   private void setValuesDay(ResponseDTO responseDTO, Long normalMinutes, Long overtimeMinutes) {

        if ( normalMinutes != null && normalMinutes > 0L) {
            responseDTO.setNormalDayTime(responseDTO.getNormalDayTime() + normalMinutes);
        }

        if (overtimeMinutes != null && overtimeMinutes > 0L) {
            responseDTO.setOvertimeDay(responseDTO.getOvertimeDay() + overtimeMinutes);

        }
   }

   private ResponseDTO setValuesNight(Long normalMinutes, Long overtimeMinutes) {
       if ( normalMinutes != null && normalMinutes > 0L) {
           responseDTO.setNormalNightTime(responseDTO.getNormalNightTime() + normalMinutes);
       }

       if (overtimeMinutes != null && overtimeMinutes > 0L) {
           responseDTO.setOvertimeNight(responseDTO.getOvertimeNight() + overtimeMinutes);
       }

       return responseDTO;
   }

   private ResponseDTO setValueSunday(Long normalMinutes, Long overtimeMinutes) {
        System.out.println("ingresa a set: " + normalMinutes + " add " + overtimeMinutes);
       if ( normalMinutes != null && normalMinutes > 0L) {
            responseDTO.setNormalSundayTime(responseDTO.getNormalSundayTime() + normalMinutes);
       }

       if (overtimeMinutes != null && overtimeMinutes > 0L) {
            responseDTO.setOvertimeSunday(responseDTO.getOvertimeSunday()+ overtimeMinutes);
       }

       return responseDTO;
   }


    private void refactorData(ResponseDTO dto) {
        dto.setNormalDayTime(utils.minutesToHours(dto.getNormalDayTime()));
        dto.setNormalNightTime(utils.minutesToHours(dto.getNormalNightTime()));
        dto.setOvertimeDay(utils.minutesToHours(dto.getOvertimeDay()));
        dto.setOvertimeNight(utils.minutesToHours(dto.getOvertimeNight()));
        dto.setNormalSundayTime(utils.minutesToHours(dto.getNormalSundayTime()));
        dto.setOvertimeSunday(utils.minutesToHours(dto.getOvertimeSunday()));
    }

}
