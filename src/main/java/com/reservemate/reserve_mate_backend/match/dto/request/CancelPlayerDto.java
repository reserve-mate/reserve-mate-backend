package com.reservemate.reserve_mate_backend.match.dto.request;

import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelPlayerDto {

    private MatchPlayer matchPlayer;

}
