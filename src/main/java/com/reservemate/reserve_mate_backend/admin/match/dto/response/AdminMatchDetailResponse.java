package com.reservemate.reserve_mate_backend.admin.match.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
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
@Getter
@Setter
@Builder
public class AdminMatchDetailResponse {

    private Long matchId;               // 매치 일련번호
    private String matchTitle;          // 매치 제목
    private SportType sportType;        // 매치 종목
    private MatchStatus matchStatus;    // 매치 상태
    private String facilityName;        // 매치 시설
    private String facilityCourt;       // 매치 코트
    private String address;             // 매치 주소
    private LocalDate matchDate;        // 매치 날짜
    private int matchTime;              // 매치 시간
    private int endTime;                // 매치 끝 시간
    private int teamCapacity;           // 매치 총원
    private int matchPrice;             // 매치 가격
    private String description;         // 매치 설명
    private List<AdminPlayer> adminPlayers;

    public static AdminMatchDetailResponse getAdminMatchDetailResponse(Match match, List<MatchPlayer> players) {

        return AdminMatchDetailResponse.builder()
            .matchId(match.getMatchId())
            .matchTitle(match.getMatchName())
            .sportType(match.getFacility().getSportType())
            .matchStatus(match.getMatchStatus())
            .facilityName(match.getFacility().getName())
            .facilityCourt(match.getCourt().getName())
            .address(match.getFacility().getAddress().getFullAddress())
            .matchDate(match.getMatchDate())
            .matchTime(match.getMatchTime())
            .endTime(match.getEndTime())
            .teamCapacity(match.getTeamCapacity())
            .matchPrice(match.getMatchPrice())
            .description(match.getDescription())
            .adminPlayers(AdminPlayer.getAdminPlayers(players))
            .build();
    }

    // 관리자 참가자 목록
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class AdminPlayer {

        private Long payerId;               // 플레이어 일련번호
        private String userName;            // 플레이어 이름
        private String email;               // 플레이어 이메일
        private String phone;               // 플레이어 전화번호
        private LocalDate joinDate;         // 플레이어 참가일

        /* 배열 데이터 전달 */
        public static List<AdminPlayer> getAdminPlayers(List<MatchPlayer> players) {
            return players.stream()
                .map(AdminPlayer::getAdminPlayer).toList();
        }

        /* AdminPlayer 데이터 담기 */
        private static AdminPlayer getAdminPlayer(MatchPlayer matchPlayer) {

            AdminPlayer player = AdminPlayer.builder()
                .payerId(matchPlayer.getPlayerId())
                .userName(matchPlayer.getUser().getName())
                .email(matchPlayer.getUser().getEmail())
                .phone(matchPlayer.getUser().getPhone())
                .joinDate(matchPlayer.getUpdatedAt().toLocalDate())
                .build();

            return player;
        }

    }

}
