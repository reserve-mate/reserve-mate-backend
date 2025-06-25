package com.reservemate.reserve_mate_backend.match.dto.respone;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.match.domain.EjectionReason;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MatchHistroyResponse {

    private Long matchId;
    private Long playerId;
    private String matchName;
    private SportType sportType;
    private PlayerStatus playerStatus;
    private MatchStatus matchStatus;
    private Long facilityId;
    private String facilityName;
    private String address;
    private LocalDate matchDate;
    private Integer matchTime;
    private Integer endTime;
    private Integer matchPrice;
    private Integer teamCapacity;
    private Long playerCnt;
    private Long reviewId;

    private EjectionReason ejectReason;

    // 매치 이력 노출
    public static MatchHistroyResponse getMatchHistroyResponse(MatchPlayer matchPlayer, int playerCnt) {
        Match match = matchPlayer.getMatch();

        return MatchHistroyResponse.builder()
            .matchId(match.getMatchId())
            .playerId(matchPlayer.getPlayerId())
            .matchName(match.getMatchName())
            .sportType(match.getCourt().getSportType())
            .playerStatus(matchPlayer.getStatus())
            .matchStatus(match.getMatchStatus())
            .facilityId(match.getFacility().getId())
            .facilityName(match.getFacility().getName())
            .address(match.getFacility().getAddress().getFullAddress())
            .matchDate(match.getMatchDate())
            .matchTime(match.getMatchTime())
            .endTime(match.getEndTime())
            .matchPrice(match.getMatchPrice())
            .teamCapacity(match.getTeamCapacity())
            .playerCnt(Long.valueOf(playerCnt))
            .ejectReason(matchPlayer.getRemovalReason())
            .build();
    }
}
