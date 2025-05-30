package com.reservemate.reserve_mate_backend.common.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.reservation.service.ReserveCUDService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class ReservationScheduleService {

    private final ReserveCUDService reserveCUDService;

    /* 시간마다 예약 상태 변경 */
    @Scheduled(cron = "0 0 6-23 * * *", zone = "Asia/Seoul")
    public void chgConfirm() {
        reserveCUDService.chgConfirm();
    }

}
