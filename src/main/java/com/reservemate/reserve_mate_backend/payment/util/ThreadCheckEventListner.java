package com.reservemate.reserve_mate_backend.payment.util;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.match.dto.request.ApplyPlayerDto;
import com.reservemate.reserve_mate_backend.match.dto.request.CancelPlayerDto;
import com.reservemate.reserve_mate_backend.match.service.MatchPlayerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class ThreadCheckEventListner {

    private final MatchPlayerService matchPlayerService;

    /* 결제 성공 시 매치 플레이어 등록 */
    @EventListener
    public void registMatchPlayer(ApplyPlayerDto playerDto) {
        log.info("결제 성공 시 매치 플레이어 등록");
        matchPlayerService.applyForMatch(playerDto);
    }

    /*  */
    @EventListener
    public void cancelMatchPlayer(CancelPlayerDto cancelPlayerDto) {
        log.info("매치 결제 취소 성공 시 매치 취소");
        matchPlayerService.cancelMatchRequest(cancelPlayerDto);
    }

}
