package com.reservemate.reserve_mate_backend.payment.dto.request;

import java.util.List;

import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchCancelPaymentRequest {

    private List<MatchPlayer> players;

}
