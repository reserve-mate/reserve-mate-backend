package com.reservemate.reserve_mate_backend.match.dto.request;

import java.util.List;

import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerOngingRequest {

    private List<MatchPlayer> matchPlayers;
    private PlayerStatus playerStatus;

}
