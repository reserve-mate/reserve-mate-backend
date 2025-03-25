package com.reservemate.reserve_mate_backend.match.dto.request;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ApplyMatchDto {

    private Long matchId;
    private Long userId;

    public MatchPlayer toMatchPlayer(User user, Match match){
        MatchPlayer matchPlayer = MatchPlayer.builder()
        .user(user)
        .match(match)
        .status(PlayerStatus.APPLY)
        .build();
        return matchPlayer;
    }

}
