package com.reservemate.reserve_mate_backend.match.dto.respone;

import java.time.LocalDate;
import java.util.List;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
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
    private List<MatchPlayerDto> playerDtos;

    public static MatchDetailDto toMatchDetailDto(Match match, String userName, String phone, List<MatchPlayer> matchPlayers) {
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
            .playerCnt(matchPlayers.size())
            .playerDtos(MatchPlayerDto.toMatchPlayerDtos(matchPlayers))
            .build();
        return detailDto;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    static class MatchPlayerDto{
        private Long playerId;
        private String userName;

        public static List<MatchPlayerDto> toMatchPlayerDtos(List<MatchPlayer> matchPlayers){
            
            List<MatchPlayerDto> playerDtos = matchPlayers.stream()
            .map(MatchPlayerDto::toMatchPlayerDto).toList();

            return playerDtos;
        }

        private static MatchPlayerDto toMatchPlayerDto(MatchPlayer matchPlayer){
            return MatchPlayerDto.builder()
            .playerId(matchPlayer.getPlayerId())
            .userName(matchPlayer.getUser().getName())
            .build();
        }
    }

}
