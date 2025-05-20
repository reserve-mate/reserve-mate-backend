package com.reservemate.reserve_mate_backend.common.schedule;

import java.time.LocalTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.match.service.MatchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class ScheduleService {

    private final MatchService matchService;

    @Scheduled(cron = "0 0 6-23 * * *")
    public void endBeforeMatch() {
        log.info("---------" + LocalTime.now().getHour() + "시 ---------");
        matchService.endBeforeMatch();
    }

}
