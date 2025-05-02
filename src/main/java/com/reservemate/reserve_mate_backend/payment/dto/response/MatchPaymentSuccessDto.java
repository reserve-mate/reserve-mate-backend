package com.reservemate.reserve_mate_backend.payment.dto.response;

import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.match.domain.Match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MatchPaymentSuccessDto extends PaymentResponse {

    private String matchName;
    private String matchDate;
    private int matchTime;
    private int matchEndTime;
    private String facilityCourt;

    public MatchPaymentSuccessDto(String stauts, Match match) {
        super(stauts);
        this.matchName = match.getMatchName();
        this.matchDate = Utils.localDateFormatWeek(match.getMatchDate());
        this.matchTime = match.getMatchTime();
        this.matchEndTime = match.getEndTime();
        this.facilityCourt = match.getFacility().getName() + " " + match.getCourt().getName();
    }

}
