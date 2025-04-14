package com.reservemate.reserve_mate_backend.payment.dto.request;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CancelPaymentDto {

    private MatchPlayer matchPlayer;
    private String cancelReason;

    /* 회원 정보 */
    public User getUser() {
        return matchPlayer.getUser();
    }

    public Match getMatch() {
        return matchPlayer.getMatch();
    }

}
