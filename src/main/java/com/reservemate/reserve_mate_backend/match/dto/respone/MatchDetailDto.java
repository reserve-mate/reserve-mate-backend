package com.reservemate.reserve_mate_backend.match.dto.respone;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;

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
public class MatchDetailDto {

    private Long matchId;
    private String manager;
    private MatchStatus matchStatus;
    private int teamCapacity;
    private String description;
    private LocalDate matchDate;
    private int matchTime;
    private int matchPrice;
    private int playerCnt;
    private String phone;
    private String userName;

    public static MatchDetailDto toMatchDetailDto(Match match, String userName, String phone, int playerCnt){
        MatchDetailDto detailDto = MatchDetailDto.builder()
        .matchId(match.getMatchId())
        .manager(match.getManager())
        .matchStatus(match.getMatchStatus())
        .teamCapacity(match.getTeamCapacity())
        .description(match.getDescription())
        .matchDate(match.getMatchDate())
        .matchTime(match.getMatchTime())
        .matchPrice(match.getMatchPrice())
        .phone(phone)
        .userName(userName)
        .playerCnt(playerCnt)
        .build();
        return detailDto;
    }

}
