package com.reservemate.reserve_mate_backend.payment.dto.request;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplyPlayerDto {

    private User user;
    private Match match;

}
